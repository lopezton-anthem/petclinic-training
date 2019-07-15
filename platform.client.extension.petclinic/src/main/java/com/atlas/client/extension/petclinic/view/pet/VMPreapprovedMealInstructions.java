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
package com.atlas.client.extension.petclinic.view.pet;

import java.time.ZonedDateTime;
import java.util.List;

import com.antheminc.oss.nimbus.domain.defn.Execution.Config;
import com.antheminc.oss.nimbus.domain.defn.MapsTo.Path;
import com.antheminc.oss.nimbus.domain.defn.MapsTo;
import com.antheminc.oss.nimbus.domain.defn.Model;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Button;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.ButtonGroup;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.CardDetail;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.FieldValue;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Grid;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.GridColumn;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.GridRowBody;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Link;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.LinkMenu;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Section;
import com.antheminc.oss.nimbus.domain.defn.extension.Content.Label;
import com.atlas.client.extension.petclinic.core.MealInstruction;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.RowBody;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.VCD1;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.VCDBody;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.VLM;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Tony Lopez
 *
 */
@Model
@Getter
@Setter
public class VMPreapprovedMealInstructions {

	@Config(url = "<!#this!>/../_process?fn=_setByRule&rule=togglemodal")
	@Button(style = Button.Style.PLAIN, type = Button.Type.button)
	private String closeModal;

	@Section
	private VS vs;
	
	@Model @Getter @Setter
	public static class VS {

		private MealInstruction mealInstructionRequest;
		
		@Config(url = "/vpAddEditPet/vtAddEditPet/vmPreapprovedMealInstructions/vs/mealInstructions.m/_process?fn=_set&url=/p/mealinstruction/_search?fn=example&rawPayload=<!json(/../mealInstructionRequest)!>")
		@Grid(headerCheckboxToggleAllPages = true, pageSize = "7", onLoad = true, rowSelection = true, expandableRows = true, postButton=true, 
				postButtonUri = "/petview/vpAddEditPet/vtAddEditPet/vmPreapprovedMealInstructions/vs/addAuthorizations",
				postButtonTargetPath = "temp_ids", postButtonLabel = "Add Authorization")
		@Label("Preapproved Meal Instructions")
		@MapsTo.Path(linked = false)
		private List<PreapprovedMealInstructionLineItem> mealInstructions;
		
		@Config(url = "/vpAddEditPet/vtAddEditPet/vmPreapprovedMealInstructions/vs/tempIds/_replace")
		@Config(url = "/vpAddEditPet/vtAddEditPet/vsAddEditPet/vfAddEditPet/mealInstructions/_process?fn=_mealInstructionConversion")
		@Config(url = "/vpAddEditPet/vtAddEditPet/vmPreapprovedMealInstructions/_process?fn=_setByRule&rule=togglemodal")
		private String addAuthorizations;
		
		@ButtonGroup(cssClass="oneColumn right")
		private CancelButtonGroup buttonGroup;
		
		@MapsTo.Path(linked = false)
		private TempIds tempIds;
	}
	
	@Model
	@Getter
	@Setter
	public static class CancelButtonGroup {

		@Button
		@Label("Cancel")
		@Config(url = "/vpAddEditPet/vtAddEditPet/vmPreapprovedMealInstructions/_process?fn=_setByRule&rule=togglemodal")
		private String cancel;
	}
		
	@MapsTo.Type(MealInstruction.class)
	@Getter @Setter
	public static class TempIds {
		
		private String[] temp_ids;
	}
	
	@MapsTo.Type(MealInstruction.class)
	@Getter @Setter @ToString
	public static class PreapprovedMealInstructionLineItem {
		
		@Label("Food Name")
		@GridColumn(placeholder = "--")
		@Path
		private String name;
		
		@Label("Amount")
		@GridColumn(placeholder = "--")
		@Path
		private String amount;
		
		@Label("Time of Day")
		@GridColumn(placeholder = "--")
		@Path
		private String timeOfDay;
	}
}
