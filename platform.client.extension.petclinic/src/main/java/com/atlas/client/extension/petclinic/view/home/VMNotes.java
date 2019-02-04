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
package com.atlas.client.extension.petclinic.view.home;

import javax.validation.constraints.NotNull;

import com.antheminc.oss.nimbus.domain.defn.Execution.Config;
import com.antheminc.oss.nimbus.domain.defn.MapsTo;
import com.antheminc.oss.nimbus.domain.defn.MapsTo.Path;
import com.antheminc.oss.nimbus.domain.defn.Model;
import com.antheminc.oss.nimbus.domain.defn.Model.Param.Values;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Button;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.ButtonGroup;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.ComboBox;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Form;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.RichText;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.RichText.ToolbarFeature;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.Section;
import com.antheminc.oss.nimbus.domain.defn.ViewConfig.TextBox;
import com.antheminc.oss.nimbus.domain.defn.extension.Content.Label;
import com.antheminc.oss.nimbus.domain.defn.extension.EnableConditional;
import com.antheminc.oss.nimbus.domain.defn.extension.LabelConditional;
import com.antheminc.oss.nimbus.domain.defn.extension.ParamContext;
import com.antheminc.oss.nimbus.domain.defn.extension.VisibleConditional;
import com.atlas.client.extension.petclinic.core.CodeValueTypes.EditorKeyValueReplacors;
import com.atlas.client.extension.petclinic.core.CodeValueTypes.NoteTypes;
import com.atlas.client.extension.petclinic.core.Note;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Tony Lopez
 *
 */
@Model
@Getter @Setter
public class VMNotes {

	@Config(url = "<!#this!>/../_process?fn=_setByRule&rule=togglemodal")
	@Button(style = Button.Style.PLAIN, type = Button.Type.button)
	private String closeModal;
	
	@VisibleConditional(when = "state == 'add'", targetPath = "/../vsMain/vfAddNote")
	@VisibleConditional(when = "state == 'readonly'", targetPath = "/../vsMain/vfViewNote")
	@LabelConditional(targetPath = "/../", condition = {
		@LabelConditional.Condition(when = "state == 'add'", then = @Label("Add Note")),
		@LabelConditional.Condition(when = "state == 'readonly'", then = @Label("View Note"))
	})
	private String mode;
	
	@Section
	private VSMain vsMain;
	
	@Model
	@Getter @Setter
	public static class VSMain{
		
		@Form(cssClass="oneColumn")
		@Path(linked = false)
		private VFAddNote vfAddNote;
		
		@Form(cssClass="oneColumn")
		@Path(linked = false)
		private VFViewNote vfViewNote;
		
	}
	
	@MapsTo.Type(Note.class)
	@Getter @Setter
	public static class VFAddNote{
		
		@Label("Note Type")
		@ComboBox
		@Values(NoteTypes.class)
		@ParamContext(enabled = false, visible = true)
		@Path
		private String noteType;
		
		@Label("Type DISABLE to disable OR type HIDE to hide the noteDescription field")
		@TextBox(postEventOnChange = true)
		@VisibleConditional(when = "state != 'HIDE'", targetPath = "/../noteDescription")
		@EnableConditional(when = "state != 'DISABLE'", targetPath = "/../noteDescription")
		private String checker;
		
		@Label("Note Description")
		@NotNull
		@RichText(postEventOnChange = true, toolbarFeatures = {
			ToolbarFeature.HEADER, ToolbarFeature.FONT, ToolbarFeature.BOLD,
			ToolbarFeature.ITALIC, ToolbarFeature.UNDERLINE, ToolbarFeature.STRIKE, ToolbarFeature.COLOR,
			ToolbarFeature.BACKGROUND, ToolbarFeature.SCRIPT, ToolbarFeature.LIST, ToolbarFeature.INDENT,
			ToolbarFeature.ALIGN, ToolbarFeature.LINK, ToolbarFeature.CLEAN, ToolbarFeature.VALUES_COMBOBOX
		})
		@Values(EditorKeyValueReplacors.class)
		@Path
		private String noteDescription;
		
		@ButtonGroup
		private VBGAddNote vbg;
	}
	
	@MapsTo.Type(Note.class)
	@Getter @Setter
	public static class VFViewNote {
		
		@Label("Note Type")
		@ComboBox
		@Values(NoteTypes.class)
		@ParamContext(enabled = false, visible = true)
		@Path
		private String noteType;
		
		@Label("Note Description")
		@NotNull
		@RichText(readOnly = true)
		@Path
		private String noteDescription;
		
		@ButtonGroup
		private VBGViewNote vbg;
	}
	
	@Model @Getter @Setter
	public static class VBGAddNote {
		
		@Label(value="Submit and Add New")
		@Button(style = Button.Style.PRIMARY, type = Button.Type.submit)
		@Config(url = "<!#this!>/../../../vfAddNote/_update")
		@Config(url = "/p/notes/_new?fn=_initEntity&target=/noteDescription&json=\"<!/../noteDescription!>\"&target=/noteType&json=\"<!/../noteType!>\"")
		@Config(url = "<!#this!>/../../../vfAddNote/_delete")
		@Config(url = "/vpNotes/vtNotes/vmNotes/vsMain/vfAddNote/noteType/_process?fn=_set&value=general")
		@Config(url = "/vpNotes/vtNotes/vsNotes/notes/.m/_process?fn=_set&url=/p/notes/_search?fn=query")
		private String submitAndAddNew;
		
		@Label(value="Submit")
		@Button(style = Button.Style.PRIMARY, type = Button.Type.submit)
		@Config(url = "<!#this!>/../../../vfAddNote/_update")
		@Config(url = "/p/notes/_new?fn=_initEntity&target=/noteDescription&json=\"<!/../noteDescription!>\"&target=/noteType&json=\"<!/../noteType!>\"")
		@Config(url = "/vpNotes/vtNotes/vsNotes/notes/.m/_process?fn=_set&url=/p/notes/_search?fn=query")
		@Config(url = "<!#this!>/../../../../_process?fn=_setByRule&rule=togglemodal")
		private String submit;
		
		@Label(value = "Back")
		@Button(style = Button.Style.SECONDARY, type = Button.Type.reset)
		@Config(url = "<!#this!>/../../../../_process?fn=_setByRule&rule=togglemodal")
		private String back;
	}
	
	@Model @Getter @Setter
	public static class VBGViewNote {
		
		@Label(value = "Back")
		@Button(style = Button.Style.SECONDARY, type = Button.Type.reset)
		@Config(url = "<!#this!>/../../../../_process?fn=_setByRule&rule=togglemodal")
		private String back;
	}
}
