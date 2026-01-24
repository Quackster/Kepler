<div class="movable sticker s_{{ sticker.getProduct().getData() }}" style="left: {{ sticker.getX() }}px; top: {{ sticker.getY() }}px; z-index: {{ sticker.getZ() }}" id="sticker-{{ sticker.getId() }}">
{% if editMode %}
<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/icon_edit.gif" width="19" height="18" class="edit-button" id="sticker-{{ sticker.getId() }}-edit" />
<script language="JavaScript" type="text/javascript">
Event.observe("sticker-{{ sticker.getId() }}-edit", "click", function(e) { openEditMenu(e, {{ sticker.getId() }}, "sticker", "sticker-{{ sticker.getId() }}-edit", {% if (sticker.getUserId() != playerDetails.id) %}true{% else %}false{% endif %}); }, false);
</script>
{% endif %}
    </div>