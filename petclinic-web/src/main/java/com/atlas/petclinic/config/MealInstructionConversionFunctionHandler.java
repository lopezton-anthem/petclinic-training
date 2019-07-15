/**
 *  Copyright 2016-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.atlas.petclinic.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.antheminc.oss.nimbus.context.BeanResolverStrategy;
import com.antheminc.oss.nimbus.domain.cmd.exec.AbstractFunctionHandler;
import com.antheminc.oss.nimbus.domain.cmd.exec.ExecutionContext;
import com.antheminc.oss.nimbus.domain.defn.extension.Content.Label;
import com.antheminc.oss.nimbus.domain.model.config.extension.LabelStateEventHandler;
import com.antheminc.oss.nimbus.domain.model.state.EntityState.Param;
import com.atlas.client.extension.petclinic.core.MealInstruction;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.NoteLineItem;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.NoteLineItem.Body;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.NoteLineItem.Card;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.RowBody;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.VCD1;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.VCDBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Tony Lopez
 *
 */
public class MealInstructionConversionFunctionHandler<T, R> extends AbstractFunctionHandler<T, R> {
	
	public static final Logger LOG = LoggerFactory.getLogger(MealInstructionConversionFunctionHandler.class);
	
	public static final String DASH = "-";
	
	private final ObjectMapper objectMapper;
	private final LabelStateEventHandler labelStateEventHandler;
	
	public MealInstructionConversionFunctionHandler(BeanResolverStrategy beanResolver) {
		this.objectMapper = beanResolver.get(ObjectMapper.class);
		this.labelStateEventHandler = beanResolver.get(LabelStateEventHandler.class);
	}
	
	private void initializeDefaultLabel(Object obj) {
		
		Param<?> param = (Param<?>) obj;
		param.setLabels(new HashSet<>());
		Set<Label> labels = null;
		if (param.getConfig() != null && param.getConfig().getLabels() != null) {
			labels = param.getConfig().getLabels();
		}
		if (labels != null) {
			for(Label label : labels) {
				this.labelStateEventHandler.addLabelToState(label, param);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public R execute(ExecutionContext eCtx, Param<T> actionParameter) {
		
		// Determine whether or not any selections from the authorizations grid were made.
		int[] temp_ids = null;
		try {
			JsonNode root = this.objectMapper.readTree(eCtx.getCommandMessage().getRawPayload());
			temp_ids = this.objectMapper.treeToValue(root.path("temp_ids"), int[].class);
		} catch (IOException e) {
			LOG.error("Failed to parse temp_ids. eCtx: {}", eCtx, e);
		}
		
		if (null != temp_ids && temp_ids.length > 0) {
			
			// Gather the services to add
			List<MealInstruction> available = (List<MealInstruction>) actionParameter
					.getRootDomain().findStateByPath("/vpAddEditPet/vtAddEditPet/vmPreapprovedMealInstructions/vs/mealInstructions/.m");
			List<MealInstruction> selected = new ArrayList<>();
			for(int id: temp_ids) {
				selected.add(available.get(id));
			}
			
			// Convert the authorizations into services
			List<MealInstructionLineItem> toAdd = new ArrayList<>();
			for(MealInstruction selectedMealInsturction: selected) {
				MealInstructionLineItem addee = this.convertToMealInstructionLineItem(selectedMealInsturction);
				toAdd.add(addee);
			}
			
			// Get already added services
			List<MealInstructionLineItem> existingMealInstructions = (List<MealInstructionLineItem>) actionParameter.getLeafState();
			if (CollectionUtils.isEmpty(existingMealInstructions)) {
				existingMealInstructions = new ArrayList<>();
			}
			
			// Add the selected services
			for (MealInstructionLineItem addee : toAdd) {
				MealInstructionLineItem foundEqualItem = null;
				if (!existingMealInstructions.isEmpty()) {
					foundEqualItem = existingMealInstructions.stream()
						.filter(s -> s.getId() != null && s.getId().equals(addee.getId()))
						.filter(s -> s.getName() != null && s.getName().equals(addee.getName()))
						.filter(s -> s.getAmount() != null && s.getAmount().equals(addee.getAmount()))
						.findFirst().orElse(null);
				}

				if (foundEqualItem == null && addee.getId() != null) {
					actionParameter.findIfCollection().add(addee);
					// TODO The following is a fix for https://jira.anthem.com/browse/CMDM-25133.
					// This should be handled by the framework, but adding it here as a temporary
					// solution due to release. This should be removed once it is resolved in the
					// framework.
					int lastIdx = actionParameter.findIfCollection().size() - 1;
					actionParameter.findIfCollection().findParamByPath("/" + lastIdx + "/vlm").traverseChildren(this::initializeDefaultLabel);
					actionParameter.findIfCollection().findParamByPath("/" + lastIdx + "/rowBody/vcd1/vcdBody").traverseChildren(this::initializeDefaultLabel);
				}
			}			
		}
		
		return null;
	}
	
	/**
	 * Performs a custom conversion with some business logic applied.
	 * 
	 * @param source The core entity to convert.
	 * @return The view object.
	 */
	public static MealInstructionLineItem convertToMealInstructionLineItem(MealInstruction source) {
		final MealInstructionLineItem target = new MealInstructionLineItem();

		BeanUtils.copyProperties(source, target, "notes");
		
		target.setNotes(new ArrayList<>());
		if (null != source.getNotes()) {
			for(NoteLineItem toAdd : source.getNotes()) {
				NoteLineItem note = new NoteLineItem();
				note.setCard(new Card());
				note.getCard().setBody(new Body());
				note.getCard().getBody().setContent(toAdd.getCard().getBody().getContent());
				target.getNotes().add(note);
			}
		}
		
		if (null == target.getRowBody()) {
			target.setRowBody(new RowBody());
			target.getRowBody().setVcd1(new VCD1());
			target.getRowBody().getVcd1().setVcdBody(new VCDBody());
		}
		
		VCDBody vcdBody = target.getRowBody().getVcd1().getVcdBody();
		BeanUtils.copyProperties(source, vcdBody);
		
		return target;
	}
}
