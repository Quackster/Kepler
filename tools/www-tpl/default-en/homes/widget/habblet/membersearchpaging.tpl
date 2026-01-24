{% set firstPage = -1 %}
{% set previousPage = -1 %}
{% set nextPage = -1 %}
{% set lastPage = -1 %}

{% if currentPage >= 2 %}
	{% set firstPage = 1 %}
{% endif %}

{% if currentPage > 1 %}
	{% set previousPage = 1 %}
{% endif %}

{% if pages >= (currentPage + 1) %}
	{% set nextPage = 1 %}
{% endif %}

{% if pages >= (currentPage + 2) %}
	{% set lastPage = 1 %}
{% endif %}
			
			<div id="avatarlist-content">
				<div class="avatar-widget-list-container">
					<ul id="avatar-list-list" class="avatar-widget-list">
					{% if members > 0 %}
						{% for member in membersList %}
						<li id="avatar-list-{{ sticker.getId() }}-{{ member.getUser().getId() }}" title="{{ member.getUser().getName() }}"><div class="avatar-list-open"><a href="#" id="avatar-list-open-link-{{ sticker.getId() }}-{{ member.getUser().getId() }}" class="avatar-list-open-link"></a></div>
						<div class="avatar-list-avatar"><img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ member.getUser().getFigure() }}&size=s&direction=4&head_direction=4&crr=0&gesture=&frame=1" alt="" /></div>
						<h4><a href="{{ site.sitePath }}/home/{{ member.getUser().getName() }}">{{ member.getUser().getName() }}</a></h4>
						<p class="avatar-list-birthday">{{ member.getUser().getCreatedAt() }}</p>
						
						{% if member.getMemberRank().getRankId() == 3 %}
							<p><img src="{{ site.staticContentPath }}/web-gallery/images/groups/owner_icon.gif" alt="" class="avatar-list-groupstatus" />
						{% endif %}
						{% if member.getMemberRank().getRankId() == 2 %}
							<p><img src="{{ site.staticContentPath }}/web-gallery/images/groups/administrator_icon.gif" alt="" class="avatar-list-groupstatus" />
						{% endif %}
						{% if member.isFavourite(group.id) %}
							<img src="{{ site.staticContentPath }}/web-gallery/images/groups/favourite_group_icon.gif" alt="" class="avatar-list-groupstatus" />
						{% endif %}
						</p>
						</li>
						{% endfor %}
					{% else %}
					You don't have any members :(
					{% endif %}
					</ul>
					<div id="avatar-list-info" class="avatar-list-info">
						<div class="avatar-list-info-close-container">
							<a href="#" class="avatar-list-info-close"></a>
							</div>
						<div class="avatar-list-info-container"></div>
					</div>
				</div>
				<div id="avatar-list-paging">
					{% if members > 0 %}
						{{ currentPage }} - {{ membersList|length }} / {{ pages }}    
						<br/>
					
						{% if (firstPage != -1) or (previousPage != -1) %}
						<a href="#" class="avatar-list-paging-link" id="avatarlist-search-first" >First</a> |
						<a href="#" class="avatar-list-paging-link" id="avatarlist-search-previous" >&lt;&lt;</a> |
						{% else %}
						First | &lt;&lt; |
						{% endif %}
					
						{% if (lastPage != -1) or (nextPage != -1) %}
						<a href="#" class="avatar-list-paging-link" id="avatarlist-search-next" >&gt;&gt;</a> |
						<a href="#" class="avatar-list-paging-link" id="avatarlist-search-last" >Last</a>
						{% else %}
						&gt;&gt; | Last
						{% endif %}
					{% else %}
					0 - 0
					{% endif %}
					<input type="hidden" id="pageNumber" value="{{ currentPage }}" />
					<input type="hidden" id="totalPages" value="{{ pages }}" />
				</div>

				<script type="text/javascript">
				document.observe("dom:loaded", function() {
					window.widget{{ sticker.getId() }} = new MemberWidget('{{ group.getId() }}', '{{ sticker.getId() }}');
				});
				</script>
			</div>