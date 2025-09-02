<table border="0" cellpadding="0" cellspacing="0" width="100%" class="group-postlist-list" id="group-postlist-list">
<tr>
    <td class="post-header-link" valign="top" style="width: 148px;"></td>
    <td class="post-header-name" valign="top"></td>    
</tr>
<tr>
	<td colspan="3" class="post-list-row-container"><div id="new-topic-preview"></div></td>
</tr>

<tr class="new-topic-entry-label" id="new-topic-entry-label">
	<td class="new-topic-entry-label">Topic:</td>
	<td colspan="2">
		<table border="0" cellpadding="0" cellspacing="0" style="margin: 5px; width: 98%;">
		<tr>
		<td>
	    <div class="post-list-content-element"><input type="text" size="50" id="new-topic-name" value=""/></div>
	    </td>
	    </tr>
	    </table>
    </td>
</tr>
<tr class="topic-name-error">
    <td></td>
    <td>
        <div id="topic-name-error"></div>
    </td>
</tr>
<tr id="new-post-entry-message" style="display:none;">

	<td class="new-post-entry-label"><div class="new-post-entry-label" id="new-post-entry-label">Post:</div></td>
	<td colspan="2">
		<table border="0" cellpadding="0" cellspacing="0" style="margin: 5px; width: 98%;">
		<tr>
		<td>
		<input type="hidden" id="edit-type" />
		<input type="hidden" id="post-id"  />
        <a href="#" class="preview-post-link" id="topic-form-preview">Preview &raquo;</a>
        <input type="hidden" id="spam-message" value=""/>
		<textarea id="post-message" class="new-post-entry-message" rows="5" name="message" ></textarea>
    	<script type="text/javascript">
        bbcodeToolbar = new Control.TextArea.ToolBar.BBCode("post-message");
        bbcodeToolbar.toolbar.toolbar.id = "bbcode_toolbar";
        var colors = { "red" : ["#d80000", "Red"],
            "orange" : ["#fe6301", "Orange"],
            "yellow" : ["#ffce00", "Yellow"],
            "green" : ["#6cc800", "Green"],
            "cyan" : ["#00c6c4", "Cyan"],
            "blue" : ["#0070d7", "Blue"],
            "gray" : ["#828282", "Gray"],
            "black" : ["#000000", "Black"]
        };
        bbcodeToolbar.addColorSelect("Colors", colors, false);
    </script>
<div id="linktool-inline">
    <div id="linktool-scope">
        <label for="linktool-query-input">Create link to a:</label>
        <input type="radio" name="scope" class="linktool-scope" value="1" checked="checked"/>Habbo        <input type="radio" name="scope" class="linktool-scope" value="2"/>Room        <input type="radio" name="scope" class="linktool-scope" value="3"/>Group&nbsp;
    </div>
    <div class="linktool-input">
        <input id="linktool-query" type="text" size="30" name="query" value=""/>
        <input id="linktool-find" class="search" type="submit" title="Find" value=""/>
    </div>
    <div class="clear" style="height: 0;"><!-- --></div>
    <div id="linktool-results" style="display: none">
    </div>
    <script type="text/javascript">
        linkTool = new LinkTool(bbcodeToolbar.textarea);
    </script>
</div>

	    <div id="discussion-captcha">
<h3>
<label for="bean_captcha" class="registration-text">Type in the security code shown in the image below.</label>
</h3>

<div id="captcha-code-error"></div>

<p></p>

<div class="register-label" id="captcha-reload">
    <p>
        <img src="{{ site.staticContentPath }}/web-gallery/v2/images/shared_icons/reload_icon.gif" width="15" height="15" alt=""/>
        <a id="captcha-reload-link" href="#">I can't read the code! Please give me another one.</a>
    </p>
</div>

<script type="text/javascript">
document.observe("dom:loaded", function() {
    Event.observe($("captcha-reload"), "click", function(e) {Utils.reloadCaptcha()});
});
</script>

<p id="captcha-container">
</p>

<p>
<input type="text" name="captcha" id="captcha-code" value="" class="registration-text required-captcha" />
</p>
</div>

        <div class="button-area">
            <a id="topic-form-cancel" class="new-button red-button cancel-icon" href="#"><b><span></span>Cancel</b><i></i></a>
            <a id="topic-form-save" class="new-button green-button save-icon" href="#"><b><span></span>Save</b><i></i></a>
        </div>
        </td>
        </tr>
        </table>
	</td>
</tr>

</table>
<div id="new-post-preview" style="display:none;">
</div>
