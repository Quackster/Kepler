				{% if searchResults|length > 0 %}
<ul class="habblet-list">
				{% set num = 0 %}
					{% for details in searchResults %}
						{% if num % 2 == 0 %}<li class="even {% else %}<li class="odd{% endif %} offline" homeurl="{{ site.sitePath }}/home/{{ details.getName() }}" style="background-image: url({{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ details.figure }}&size=s&direction=2&head_direction=2&crr=0&gesture=sml&frame=1)">
	            	    <div class="item">
	            		    <b>{{ details.getName() }}</b><br />

	            	    </div>
						
	            	    <div class="lastlogin">
	            	    	<b>Last visit</b><br />
	            	    		<span title="{{ details.getFormattedLastOnline().toUpperCase() }}">{{ details.getFormattedLastOnline().toUpperCase() }}</span>
	            	    </div>
	            	    <div class="tools">
							{% if messenger.hasFriend(details.id) == false %}
	            	    		<a href="#" class="add" avatarid="{{ details.id }}" title="Send friend request"></a>
							{% endif %}
	            	    </div>
	            	    <div class="clear"></div>
	                </li>
					{% set num = num + 1 %}
					{% endfor %}
</ul>
<div id="habblet-paging-avatar-habblet-list-container">
	<p id="avatar-habblet-list-container-list-paging" class="paging-navigation">
			<span class="disabled">&laquo;</span>
		{% if previousPageId > 0 %}
		<a href="#" class="avatar-habblet-list-container-list-paging-link" id="avatar-habblet-list-container-list-page-{{ previousPageId }}">{{ previousPageId }}</a>
		{% endif %}
		<span class="current">{{ currentPage }}</span>
		{% if nextPageId > 0 %}
		<a href="#" class="avatar-habblet-list-container-list-paging-link" id="avatar-habblet-list-container-list-page-{{ nextPageId }}">{{ nextPageId }}</a>
		{% endif %}
		<span class="disabled">&raquo;</span>			        
	</p>
<input type="hidden" id="avatar-habblet-list-container-pageNumber" value="{{ currentPage }}"/>
<input type="hidden" id="avatar-habblet-list-container-totalPages" value="{{ totalPages }}"/>
</div>
				{% else %}
					<div class="box-content">{{ site.siteName }} not found. Please make sure you have typed his or her name correctly and try again. </div>
				{% endif %}