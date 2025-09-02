<!-- X-JSON: {{ sticker.getId() }} -->

<div class="movable stickie n_skin_{{ sticker.getSkin() }}-c" style="left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }}" id="stickie-{{ sticker.getId() }}">
	<div class="n_skin_{{ sticker.getSkin() }}" >
		<div class="stickie-header">
			<h3>
			{% if editMode %}
<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="stickie-{{ sticker.getId() }}-edit" />
<script language="JavaScript" type="text/javascript">
Event.observe("stickie-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "stickie", "stickie-{{ sticker.getId() }}-edit"); }, false);
</script>
{% endif %}
			</h3>
			<div class="clear"></div>
		</div>
		<div class="stickie-body">
			<div class="stickie-content">
				<div class="stickie-markup">{{ sticker.getFormattedText() }}</div>
				<div class="stickie-footer">
				</div>
			</div>
		</div>
	</div>
</div>
