<table border="0" cellpadding="0" cellspacing="0" width="100%" class="group-postlist-list" id="group-postlist-list">
<tr class="post-list-index-preview">
	<td class="post-list-row-container">
			<a href="{{ site.sitePath }}/home/{{ playerDetails.id }}/id" class="post-list-creator-link post-list-creator-info">{{ playerDetails.getName() }}</a>
			{% if playerDetails.isOnline %}
            <img alt="online_anim" src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/habbo_online_anim.gif" />
			{% else %}
			<img alt="online_anim" src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/habbo_offline.gif" />
			{% endif %}
		<div class="post-list-posts post-list-creator-info">Messages: {{ userReplies }}</div>
		<div class="clearfix">
            <div class="post-list-creator-avatar">
			<img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ playerDetails.figure }}&size=b&direction=2&head_direction=2&crr=0&gesture=&frame=1" alt="" />
			</div>
			{% if hasGroup %}
            <div class="post-list-group-badge">
                <a href="{{ site.sitePath }}/groups/{{ groupId }}/id"><img src="{{ site.sitePath }}/habbo-imaging/badge/{{ groupBadge }}.gif" /></a>            </div>
			{% endif %}
			{% if hasBadge %}
            <div class="post-list-avatar-badge">
				<img src="{{ site.staticContentPath }}/c_images/album1584/{{ badge }}.gif" />			
			</div>
			{% endif %}
        </div>
        <div class="post-list-motto post-list-creator-info">
			{{ playerDetails.motto }}		</div>
	</td>
	<td class="post-list-message" valign="top" colspan="2">
            <a href="#" id="edit-post-message" class="resume-edit-link">&laquo; Edit</a>
        <span class="post-list-message-header"> {% autoescape 'html' %}{{ postName }}{% endautoescape %}</span><br />
        <span class="post-list-message-time">{{ previewDay }} ({{ previewTime }})</span>
        <div class="post-list-report-element">
        </div>
        <div class="post-list-content-element">
            {{ postMessage }}        </div>
        <div>
                <div id="discussion-captcha-preview"></div>                <div class="button-area">
  		            <a id="post-form-cancel-preview" class="new-button red-button cancel-icon" href="#"><b><span></span>Cancel</b><i></i></a>
		            <a id="post-form-save-preview" class="new-button green-button save-icon" href="#"><b><span></span>Save</b><i></i></a>
		        </div>
        </div>
	</td>
</tr>
</table>