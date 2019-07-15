/**
 *  Copyright 2016-2018 the original author or authors.
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
import com.antheminc.oss.nimbus.domain.defn.MapsTo;
import com.antheminc.oss.nimbus.domain.defn.MapsTo.Path;
import com.antheminc.oss.nimbus.domain.defn.Model;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.CardDetail;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.FieldValue;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.GridColumn;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.GridRowBody;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Link;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.LinkMenu;
import com.antheminc.oss.nimbus.domain.defn.extension.Content.Label;
import com.atlas.client.extension.petclinic.core.MealInstruction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Tony Lopez
 *
 */
@MapsTo.Type(MealInstruction.class)
@Getter @Setter @ToString
public class MealInstructionLineItem {
	
	private Long id;
	
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
	
	@GridColumn(hidden = true)
	@Path
	private List<NoteLineItem> notes;
	
	@GridRowBody
	private RowBody rowBody;
	
	@LinkMenu
	private VLM vlm;
	
	@Model
	@Getter @Setter
	public static class RowBody {
	
		@CardDetail
   		private VCD1 vcd1;
	}
	
 	@Model
 	@Getter @Setter
   	public static class VCD1 {
 		
   		@CardDetail.Body
   		VCDBody vcdBody;
   	}
	
 	@MapsTo.Type(MealInstruction.class)
   	@Getter @Setter
   	public static class VCDBody {
 		
 		@Label("How long as pet eaten food?")
 		@FieldValue(placeholder = "--")
 		@Path
 		private String lengthOfTimeEaten;
 		
 		@Label("Created By")
 		@FieldValue(placeholder = "--")
 		@Path
 		private String createdBy;
 		
 		@Label("Created Date")
 		@FieldValue(placeholder = "--")
 		@Path
 		private ZonedDateTime createdDate;
 		
 		@Label("Last Modified By")
 		@FieldValue(placeholder = "--")
 		@Path
 		private String lastModifiedBy;
 		
 		@Label("Last Modified Date")
 		@FieldValue(placeholder = "--")
 		@Path
 		private ZonedDateTime lastModifiedDate;
 	}
 	
	@Model
	@Getter @Setter
	public static class VLM {
		
		@Label("Edit")
		@Link
		@Config(url = "/vpAddEditPet/vtAddEditPet/vmMealInstruction/section/form/_get?fn=param&expr=assignMapsTo('/.d/<!#this!>/../../.m')")
		@Config(url = "/vpAddEditPet/vtAddEditPet/vmMealInstruction/_process?fn=_setByRule&rule=togglemodal")
		private String edit;
		
		@Label("Remove")
		@Link
		@Config(url = "<!#this!>/../../_delete")
		private String remove;
	}
	
	@Model @Getter @Setter
	public static class NoteLineItem {
		
		@CardDetail
		private Card card;
		
		@Model
		@Getter @Setter @ToString
		public static class Card {
			
			@CardDetail.Body
			private Body body;
		}
		
		@Model
		@Getter @Setter @ToString
		public static class Body {
			
			@FieldValue(placeholder = "--")
			@Label("Note content:")
			private String content;	
		}
	}
}
