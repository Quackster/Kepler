{% autoescape 'html' %}
		<div id="category-options" class="clearfix">
			<select id="category-list-select" name="category-list">
				<option value="0">Friends</option>
				{% for category in categories %}
					<option value="{{ category.getId() }}">{{ category.getName() }}</option>
				{% endfor %}
			</select>

<div class="friend-del"><a class="new-button red-button cancel-icon" href="#" id="delete-friends"><b><span></span>Delete selected friends</b><i></i></a></div>
<div class="friend-move"><a class="new-button" href="#" id="move-friend-button"><b><span></span>Move</b><i></i></a></div>     
        </div>
{% endautoescape %}