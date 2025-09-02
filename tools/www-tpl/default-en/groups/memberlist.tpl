<div id="group-memberlist-members-list">

<form method="post" action="#" onsubmit="return false;">
<ul class="habblet-list two-cols clearfix">

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

{% set position = "right" %}
			
{% set i = 0 %}
{% set lefts = 0 %}
{% set rights = 0 %}
{% for member in memberList %}				
	{% if i % 2 == 0 %}
		{% set position = "right" %}
		{% set rights = rights + 1 %}
	{% else %}
		{% set position = "left" %}
		{% set lefts = lefts + 1 %}
	{% endif %}
	
	{% if lefts % 2 == 0 %}
		{% set status = "even" %}
	{% else %}
		{% set status = "odd" %}
	{% endif %}

	<li class="{{ status }} online {{ position }}">
    	<div class="item" style="padding-left: 5px; padding-bottom: 4px;">
    		<div style="float: right; width: 16px; height: 16px; margin-top: 1px">
			{% if member.getMemberRank().getRankId() == 3 %}
				<img src="{{ site.staticContentPath }}/web-gallery/images/groups/owner_icon.gif" width="15" height="15" alt="" title="" />
			{% endif %}
			{% if member.getMemberRank().getRankId() == 2 %}
				<img src="{{ site.staticContentPath }}/web-gallery/images/groups/administrator_icon.gif" width="15" height="15" alt="Administrator" title="Administrator" />
			{% endif %}
			</div>
			{% if  (selfMember.getMemberRank().getRankId() <= member.getMemberRank().getRankId()) and (selfMember.getMemberRank().getRankId() >= 2) %}
			<input type="checkbox" disabled="disabled" style="margin: 0; padding: 0; vertical-align: middle" />
			{% else %}
				{% if member.getMemberRank().getRankId() == 2 %}
				<input type="checkbox" id="group-memberlist-a-{{ member.getUser().getId() }}" style="margin: 0; padding: 0; vertical-align: middle"/>
				{% elseif member.getMemberRank().getRankId() == 1 %}
				<input type="checkbox" id="group-memberlist-m-{{ member.getUser().getId() }}" style="margin: 0; padding: 0; vertical-align: middle"/>
				{% endif %}
			{% endif %}
			
			<a class="home-page-link" href="{{ site.sitePath }}/home/{{ member.getUser().getName() }}"><span>{{ member.getUser().getName() }}</span></a>
        </div>
    </li>

	{% set i = i + 1 %}
{% endfor %}
	
</ul>

</form>
<div id="member-list-pagenumbers">
{{ memberList|length }} - {{ currentPage }} / {{ pages }}
</div>
<div id="member-list-paging" style="margin-top:10px">
	{% if (firstPage != -1) or (previousPage != -1) %}
	<a href="#" class="avatar-list-paging-link" id="memberlist-search-first" >First</a> |
	<a href="#" class="avatar-list-paging-link" id="memberlist-search-previous" >&lt;&lt;</a> |
	{% else %}
	First | &lt;&lt; |
	{% endif %}

	{% if (lastPage != -1) or (nextPage != -1) %}
	<a href="#" class="avatar-list-paging-link" id="memberlist-search-next" >&gt;&gt;</a> |
	<a href="#" class="avatar-list-paging-link" id="memberlist-search-last" >Last</a>
	{% else %}
	&gt;&gt; | Last
	{% endif %}
<input type="hidden" id="pageNumberMemberList" value="{{ currentPage }}"/>
<input type="hidden" id="totalPagesMemberList" value="{{ pages }}"/>
</div>
</div>