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
package com.atlas.petclinic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.atlas.client.extension.petclinic.core.MealInstruction;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.NoteLineItem;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.NoteLineItem.Body;
import com.atlas.client.extension.petclinic.view.pet.MealInstructionLineItem.NoteLineItem.Card;

/**
 * @author Tony Lopez
 *
 */
@RestController
public class MealInstructionController {
	
	@PostMapping(value = "/**/mealinstruction/_search")
	public List<MealInstruction> getMealInstructions(@RequestBody(required = false) MealInstruction payload) {
		
//		if (null == payload) {
//			return new ArrayList<>();
//		}
		
		return this.getMockMealInstructions();
	}
	
	private List<MealInstruction> getMockMealInstructions() {
		List<MealInstruction> response = new ArrayList<>();
		response.add(new MealInstruction());
		response.get(0).setId(1L);
		response.get(0).setName("Kibbles N Bits (2 notes)");
		response.get(0).setAmount("1 cup");
		response.get(0).setLengthOfTimeEaten("1 year");
		response.get(0).setNotes(new ArrayList<>());
		response.get(0).getNotes().add(new NoteLineItem());
		response.get(0).getNotes().get(0).setCard(new Card());
		response.get(0).getNotes().get(0).getCard().setBody(new Body());
		response.get(0).getNotes().get(0).getCard().getBody().setContent("1");
		response.get(0).getNotes().add(new NoteLineItem());
		response.get(0).getNotes().get(1).setCard(new Card());
		response.get(0).getNotes().get(1).getCard().setBody(new Body());
		response.get(0).getNotes().get(1).getCard().getBody().setContent("2");
		response.add(new MealInstruction());
		response.get(1).setId(2L);
		response.get(1).setName("EzPuppyEats (1 note)");
		response.get(1).setAmount("1/2 cup");
		response.get(1).setLengthOfTimeEaten("6 months");
		response.get(1).setNotes(new ArrayList<>());
		response.get(1).getNotes().add(new NoteLineItem());
		response.get(1).getNotes().get(0).setCard(new Card());
		response.get(1).getNotes().get(0).getCard().setBody(new Body());
		response.get(1).getNotes().get(0).getCard().getBody().setContent("2");
		response.add(new MealInstruction());
		response.get(2).setId(3L);
		response.get(2).setName("Canine Chews (0 notes)");
		response.get(2).setAmount("2 cups");
		response.get(2).setLengthOfTimeEaten("1 year");
		response.get(2).setNotes(new ArrayList<>());
		return response;
	}
}
