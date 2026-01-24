{% autoescape 'html' %}
<div id="friend-list-header-container" class="clearfix">
    <div id="friend-list-header">
        <div class="page-limit">
            <div class="big-icons friend-header-icon">Friends                <br />Show
           		{% if pageLimit != 30 %}<a class="category-limit" id="pagelimit-30">30</a>{% else %}30{% endif %} |
                {% if pageLimit != 50 %}<a class="category-limit" id="pagelimit-50">50</a>{% else %}50{% endif %} |
                {% if pageLimit != 100 %}<a class="category-limit" id="pagelimit-100">100</a>{% else %}100{% endif %}
                            </div>
        </div>
    </div>
<div id="friend-list-paging">
										{% if firstPage != -1 %}
											<a href="#" class="friend-list-page" id="page-{{ firstPage }}">&lt;&lt;</a>
										{% endif %}

										{% if previousPage != -1 %}
											<a href="#" class="friend-list-page" id="page-{{ previousPage }}">{{ previousPage }}</a> |
										{% endif %}
										
										{{ currentPage }} |
										
										{% if nextPage != -1 %}
											<a href="#" class="friend-list-page" id="page-{{ nextPage }}">{{ nextPage }}</a> |
										{% endif %}
										
										{% if lastPage != -1 %}
											<a href="#" class="friend-list-page" id="page-{{ lastPage }}">&gt;&gt;</a>
										{% endif %}
    </div>


<form id="friend-list-form">
    <table id="friend-list-table" border="0" cellpadding="0" cellspacing="0">
        <thead>
            <tr class="friend-list-header">
                <th class="friend-select" />
                <th class="friend-name">

                    <a class="sort">Name</a>
                </th>
                <th class="friend-login">
                    <a class="sort">Last login</a>
                </th>
                <th class="friend-remove">Remove</th>
            </tr>

        </thead>
        <tbody>
					{% set num = 0 %}
					{% for friend in friends %}
						{% if num % 2 == 0 %}
						<tr class="odd">
						{% else %}
						<tr class="even">
						{% endif %}
               <td><input type="checkbox" name="friendList[]" value="{{ friend.getUserId() }}" /></td>
               <td class="friend-name">
                {{ friend.getUsername() }}
               </td>
               <td class="friend-login" title="{{ friend.getFormattedLastOnline() }}">{{ friend.getFormattedLastOnline() }}</td>
               <td class="friend-remove"><div id="remove-friend-button-{{ friend.getUserId() }}" class="friendmanagement-small-icons friendmanagement-remove remove-friend"></div></td>
            </tr>
						{% set num = num + 1 %}
					{% endfor %}
        </tbody>
    </table>
    <a class="select-all" id="friends-select-all" href="#">Select all</a> |
    <a class="deselect-all" href=#" id="friends-deselect-all">Deselect all</a>
</form>

<div id="category-options" class="clearfix">
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

      </div>
{% endautoescape %}