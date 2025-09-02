{% if articles|length > 0 %}
<h2>{{ header }}</h2>
<ul>
{% set num = 0 %}
{% for article in articles %}
<li>
	{% if article.getUrl() == currentArticle.getUrl() %}
	<a href="{{ site.sitePath }}/{{ articleLink }}/{{ article.getUrl() }}{{ urlSuffix }}" class="article-5">{% if article.isPublished() == false %}DRAFT: {% endif %}{{ article.title }}&nbsp;&raquo;</a>
	{% else %}
	<a href="{{ site.sitePath }}/{{ articleLink }}/{{ article.getUrl() }}{{ urlSuffix }}" class="article-{{ num }}">{% if article.isPublished() == false %}DRAFT: {% endif %}{{ article.title }}&nbsp;&raquo;</a>
	{% endif %}
</li>
{% set num = num + 1 %}

{% if num == 5 %}
{% set num = num + 1 %}
{% endif %}
{% endfor %}
</ul>
{% endif %}