{% if hasMessage == false %}
	<div class="postlist-header clearfix">
					{% if discussionTopic.isOpen() == false %}
				<span class="topic-closed"><img src="{{ site.staticContentPath }}/web-gallery/images/groups/status_closed.gif" title="Closed Thread"> Closed Thread</span>
			{% endif %}
				{% if canReplyForum %}
                    <a href="#" id="create-post-message" class="create-post-link verify-email">Post Reply</a>
										{% endif %}
					{% if (session.loggedIn) and ((playerDetails.id == discussionTopic.getCreatorId()) or hasTopicAdmin) %}
                <a href="#" id="edit-topic-settings" class="edit-topic-settings-link">Edit Thread &raquo;</a>
								{% endif %}
                <input type="hidden" id="settings_dialog_header" value="Edit Thread Settings"/>
				                    <input type="hidden" id="email-verfication-ok" value="1"/> 
    <div class="page-num-list">
		View page:
	{% if currentPage != 1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/1">&lt;&lt;</a> 
	{% endif %}

	{% if previousPage5 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage5 }}">{{ previousPage5 }}</a>
	{% endif %}

	{% if previousPage4 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage4 }}">{{ previousPage4 }}</a>
	{% endif %}

	{% if previousPage3 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage3 }}">{{ previousPage3 }}</a>
	{% endif %}

	{% if previousPage2 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage2 }}">{{ previousPage2 }}</a>
	{% endif %}

	{% if previousPage1 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage1 }}">{{ previousPage1 }}</a>
	{% endif %}
	{{ currentPage }}
	{% if nextPage1 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage1 }}">{{ nextPage1 }}</a>
	{% endif %}

	{% if nextPage2 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage2 }}">{{ nextPage2 }}</a>
	{% endif %}

	{% if nextPage3 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage3 }}">{{ nextPage3 }}</a>
	{% endif %}

	{% if nextPage4 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage4 }}">{{ nextPage4 }}</a>
	{% endif %}

	{% if nextPage5 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage5 }}">{{ nextPage5 }}</a>
	{% endif %}

	{% if pages != currentPage %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ pages }}">&gt;&gt;</a> 
	{% endif %}
	</div>
    </div>
{% endif %}
<table border="0" cellpadding="0" cellspacing="0" width="100%" class="group-postlist-list" id="group-postlist-list">
{% if hasMessage %}
                    <div id="discussionbox">
<div class="box-content">

<h1>Oops!</h1>

<p>
        {{ message }} <br/>
</p>

</div>
                    </div>
{% else %}
{% set num = 0 %}
{% for reply in replyList %}
	{% if num % 2 == 0 %}
	<tr class="post-list-index-even">
	{% else %}
	<tr class="post-list-index-odd">
	{% endif %}
	<td class="post-list-row-container">
		<a href="{{ site.sitePath }}/home/{{ reply.getUserId() }}/id" class="post-list-creator-link post-list-creator-info">{{ reply.getUsername() }}</a>

		{% if reply.isOnline() %}
        <img alt="online_anim" src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/habbo_online_anim.gif" />
		{% else %}
		<img alt="online_anim" src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/habbo_offline.gif" />
		{% endif %}
		<div class="post-list-posts post-list-creator-info">Messages: {{ reply.getForumMessages() }}</div>
		<div class="clearfix">
            <div class="post-list-creator-avatar"><img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ reply.getFigure() }}&size=b&direction=2&head_direction=2&crr=0&gesture=&frame=1" alt="" /></div>
            <div class="post-list-group-badge">
			{% if reply.hasGroupBadge() %}
			<a href="{{ site.sitePath }}/groups/{{ reply.getGroupId() }}/id">
				<img src="{{ site.sitePath }}/habbo-imaging/badge/{{ reply.getGroupBadge() }}.gif" /></a>           
			</div>
			{% endif %}
			{% if reply.hasBadge() %}
            <div class="post-list-avatar-badge">
				<img src="{{ site.staticContentPath }}/c_images/album1584/{{ reply.getEquippedBadge() }}.gif" />			
			</div>
			{% endif %}
        </div>
        <div class="post-list-motto post-list-creator-info"></div>
	</td>
	<td class="post-list-message" valign="top" colspan="2"> 
		{% if (session.loggedIn) and 
				(discussionTopic.isOpen()) and 
				((playerDetails.id != reply.getUserId()) or hasTopicAdmin) and 
				(reply.isDeleted() == false) %}
		<a href="#" class="quote-post-link verify-email" id="quote-post-{{ reply.getId() }}-message">Quote</a>    
		{% endif %}
		
		{% if (session.loggedIn) and 
				(discussionTopic.isOpen()) and 
				(playerDetails.id == reply.getUserId()) and 
				(reply.isDeleted() == false) %}                  
		<a href="#" class="edit-post-link verify-email" id="edit-post-{{ reply.getId() }}-message">Edit</a>
		{% endif %}
		
		<span class="post-list-message-header">{% autoescape 'html' %}{% if reply.getId() != firstReply %}RE: {% endif %}{{ discussionTopic.getTopicTitle() }}{% endautoescape %}</span><br />
        <span class="post-list-message-time">{{ reply.getCreatedDate('MMM dd, yyyy') }} ({{ reply.getCreatedDate('h:mm a') }})</span>
		{% if reply.isDeleted() == false %}
		{% if ((session.loggedIn) and (playerDetails.id != reply.getUserId())) %}
        <div class="post-list-report-element">  
			<a href="#" id="report-post-{{ reply.getId() }}" class="create-report-button report-post"></a>
		</div>
		{% endif %}
		
		{% if ((session.loggedIn) and (playerDetails.id != reply.getUserId()) or (discussionTopic.isOpen())) or (hasTopicAdmin) %}
		{% endif %}
		
		{% if ((session.loggedIn) and (playerDetails.id == reply.getUserId())) or (hasTopicAdmin) %}
       <div class="post-list-report-element">
            <a href="#" id="delete-post-{{ reply.getId() }}" class="delete-button delete-post"></a>   
		
		</div>
		{% endif %}
		{% endif %}
        <div class="post-list-content-element">
			
			{% if reply.isEdited() or reply.isDeleted() %}
			<span class="post-list-message-edited">Last edited: {{ reply.getEditedDate('MMM dd, yyyy') }} ({{ reply.getEditedDate('h:mm a') }})</span>
			<br/>
			{% endif %}
			            {%if reply.isDeleted() %}[Post deleted]{% else %}{{ reply.getFormattedMessage() }}{% endif %}                <input type="hidden" id="{{ reply.getId() }}-message" value="{% autoescape 'html' %}{{ reply.getMessage() }}{% endautoescape %}" />
        </div>
        <div>
        </div>

	</td>

</tr>
{% set num = num + 1 %}
{% endfor %}

	<tr class="postlist-leaderboard">
	    <td colspan="3">    <div class="habblet ad-forum-leaderboard">
    
    </div>
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
        <a href="#" class="preview-post-link" id="post-form-preview">Preview &raquo;</a>
        <input type="hidden" id="spam-message" value="Spam detected!"/>
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
            <a id="post-form-cancel" class="new-button red-button cancel-icon" href="#"><b><span></span>Cancel</b><i></i></a>
            <a id="post-form-save" class="new-button green-button save-icon" href="#"><b><span></span>Save</b><i></i></a>
        </div>

        </td>
        </tr>
        </table>
	</td>
</tr>
{% endif %}
</table>
 {% if hasMessage == false %}
<div id="new-post-preview" style="display:none;">
</div>
    <div class="postlist-footer clearfix">
					{% if canReplyForum %}
                    <a href="#" id="create-post-message" class="create-post-link verify-email">Post Reply</a>
				{% endif %}
				
				{% if discussionTopic.isOpen() == false %}
				<span class="topic-closed"><img src="{{ site.staticContentPath }}/web-gallery/images/groups/status_closed.gif" title="Closed Thread"> Closed Thread</span>
			{% endif %}
			

</p>            <div class="page-num-list">
<input type="hidden" id="current-page" value="{{ currentPage }}"/>
		View page:
	{% if currentPage != 1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/1">&lt;&lt;</a> 
	{% endif %}

	{% if previousPage5 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage5 }}">{{ previousPage5 }}</a>
	{% endif %}

	{% if previousPage4 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage4 }}">{{ previousPage4 }}</a>
	{% endif %}

	{% if previousPage3 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage3 }}">{{ previousPage3 }}</a>
	{% endif %}

	{% if previousPage2 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage2 }}">{{ previousPage2 }}</a>
	{% endif %}

	{% if previousPage1 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ previousPage1 }}">{{ previousPage1 }}</a>
	{% endif %}
	{{ currentPage }}
	{% if nextPage1 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage1 }}">{{ nextPage1 }}</a>
	{% endif %}

	{% if nextPage2 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage2 }}">{{ nextPage2 }}</a>
	{% endif %}

	{% if nextPage3 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage3 }}">{{ nextPage3 }}</a>
	{% endif %}

	{% if nextPage4 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage4 }}">{{ nextPage4 }}</a>
	{% endif %}

	{% if nextPage5 != -1 %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ nextPage5 }}">{{ nextPage5 }}</a>
	{% endif %}

	{% if pages != currentPage %}
	<a href="{{ group.generateClickLink() }}/discussions/{{ discussionTopic.getId() }}/id/page/{{ pages }}">&gt;&gt;</a> 
	{% endif %}
	</div>
    </div>

<script type="text/javascript">
L10N.put("myhabbo.discussion.error.topic_name_empty", "Topic name cannot be empty");
L10N.put("register.error.security_code", "The security code was invalid, please try again.");
Discussions.initialize("{{ group.getId() }}", "", "{{ discussionTopic.getId() }}");
Discussions.captchaPublicKey = "1567252727";
Discussions.captchaUrl = "{{ site.siteContentPath }}/captcha.jpg?t=";
</script>
{% endif %}