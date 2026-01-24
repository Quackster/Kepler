			<form name="tag_search_form" action="{{ site.sitePath }}/tag/search" class="search-box">
				<input type="text" name="tag" id="search_query" value="" class="search-box-query" style="float: left"/>
				<a onclick="$(this).up('form').submit(); return false;" href="#" class="new-button search-icon" style="float: left"><b><span></span></b><i></i></a>	
			</form>
			<div class="clearfix"></div>
			{% if tagList.size() equals 0 %}
				<p class="search-result-count">No results found.</p>
			{% else %}
				<p class="search-result-count">{{ pageId }} - {{ totalTagUsers|length }} / {{ totalCount }}</p>
			{% endif %}
			{% autoescape 'html' %}
			{% if tagSearchAdd != "" %}
			<p id="tag-search-add" class="clearfix">
				<span style="float:left">Tag yourself with:</span>
				<a id="tag-search-tag-add" href="#" class="new-button" style="float:left" onclick="TagHelper.addThisTagToMe('{{ tagSearchAdd | replace({"'": "\'"}) }}',false);return false;">
					<b>{{ tagSearchAdd }}</b>
					<i></i>
				</a>
			</p>
			{% endif %}
			<p class="search-result-divider"></p>

			<table border="0" cellpadding="0" cellspacing="0" width="100%" class="search-result">
				<tbody>
				{% set num = 0 %}
				{% if tagList.size() > 0 %}
				{% for habboTag in tagList %}
					{% set num = num + 1 %}
					{% set tags = habboTag.getTagList() %}

					{% if num % 2 == 0 %}
					<tr class="even">
					{% else %}
					<tr class="odd">
					{% endif %}
					
					{% if habboTag.getUserId() > 0 %}
					{% set player = habboTag.getUserData() %}
					<td class="image" style="width:39px;">
						<img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ player.figure }}&size=s" alt="" align="left"/>
					</td>
					<td class="text">
						<a href="{{ site.sitePath }}/home/{{ player.getName() }}" class="result-title">{{ player.getName() }}</a><br/>
						<span class="result-description">{{ player.getMotto }}</span>

						<ul class="tag-list">
						{% for userTag in tags %}
							<li><a href="{{ site.sitePath }}/tag/{{ userTag }}" class="tag" style="font-size:10px">{{ userTag }}</a> </li>
						{% endfor %}
						</ul>
					</td>
					{% endif %}
					{% if habboTag.getGroupId() > 0 %}
					{% set group = habboTag.getGroupData() %}
					<td class="image" style="width:39px;">
						<img src="{{ site.sitePath }}/habbo-imaging/badge/{{ group.getBadge() }}.gif" alt="" align="left"/>
					</td>
					<td class="text">
						<a href="{{ group.generateClickLink() }}" class="result-title">{{ group.getName() }}</a><br/>
						<span class="result-description">{{ group.getDescription() }}</span>

						<ul class="tag-list">
						{% for userTag in tags %}
							<li><a href="{{ site.sitePath }}/tag/{{ userTag }}" class="tag" style="font-size:10px">{{ userTag }}</a> </li>
						{% endfor %}
						</ul>
					</td>
					{% endif %}
				</tr>
				{% endfor %}
				{% endif %}
				</tbody>
			</table>
			{% endautoescape %}
			<p class="search-result-navigation">
			{% if showFirst %}
			<a href="{{ site.sitePath }}/tag/{{ tag }}?pageNumber={{ showFirstPage }}"><<</a>
			{% endif %}
			{% if showOldest %}
			<a href="{{ site.sitePath }}/tag/{{ tag }}?pageNumber={{ pageId - 2 }}">{{ pageId - 2 }}</a>
			{% endif %}
			{% if showOlder %}
			<a href="{{ site.sitePath }}/tag/{{ tag }}?pageNumber={{ pageId - 1 }}">{{ pageId - 1 }}</a>
			{% endif %}
			{{ pageId }}
			{% if showNewer %}
			<a href="{{ site.sitePath }}/tag/{{ tag }}?pageNumber={{ pageId + 1 }}">{{ pageId + 1 }}</a>
			{% endif %}
			{% if showNewest %}
			<a href="{{ site.sitePath }}/tag/{{ tag }}?pageNumber={{ pageId + 2 }}">{{ pageId + 2 }}</a>
			{% endif %}
			{% if showLast %}
			<a href="{{ site.sitePath }}/tag/{{ tag }}?pageNumber={{ showLastPage }}">>></a>
			{% endif %}
			</p>