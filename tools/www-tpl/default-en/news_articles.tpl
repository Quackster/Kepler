
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	{% if newsPage == "custom_event" %}
	<title>{{ site.siteName }}: Space Academy Points</title>	
	{% else %}
	<title>{{ site.siteName }}: News ~ {{ ("currentArticle" is present) ? currentArticle.title : "No news" }} </title>
	{% endif %}
	
<script type="text/javascript">
var andSoItBegins = (new Date()).getTime();
</script>
    <link rel="shortcut icon" href="{{ site.staticContentPath }}/web-gallery/v2/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="alternate" type="application/rss+xml" title="Retro: RSS" href="{{ site.sitePath }}/articles/rss.xml" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs2.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/visual.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/common.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/fullcontent.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/style.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/buttons.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/boxes.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/tooltips.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/local/com.css" type="text/css" />

<script src="{{ site.staticContentPath }}/web-gallery/js/local/com.js" type="text/javascript"></script>

<script type="text/javascript">
document.habboLoggedIn = {{ session.loggedIn }};
var habboName = "a";
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
var habboPartner = "";
window.name = "habboMain";
if (typeof HabboClient != "undefined") { HabboClient.windowName = "client"; }

</script>

<meta name="description" content="Join the world's largest virtual hangout where you can meet and make friends. Design your own rooms, collect cool furniture, throw parties and so much more! Create your FREE Retro today!" />
<meta name="keywords" content="Retro, virtual, world, join, groups, forums, play, games, online, friends, teens, collecting, social network, create, collect, connect, furniture, virtual, goods, sharing, badges, social, networking, hangout, safe, music, celebrity, celebrity visits, cele" />

{% if "currentArticle" is present %}
<meta name="twitter:title" content="{{ currentArticle.title }}" />
<meta name="twitter:description" content="{{ currentArticle.shortstory }}" />
{% if currentArticle.articleImage != "" %}
<meta name="twitter:image" content="{{ currentArticle.articleImage }}" />
{% endif %}
{% endif %}

<!--[if IE 8]>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/ie8.css" type="text/css" />
<![endif]-->
<!--[if lt IE 8]>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/ie.css" type="text/css" />
<![endif]-->
<!--[if lt IE 7]>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/ie6.css" type="text/css" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/pngfix.js" type="text/javascript"></script>
<script type="text/javascript">
try { document.execCommand('BackgroundImageCache', false, true); } catch(e) {}
</script>

<style type="text/css">
body { behavior: url({{ site.staticContentPath }}/web-gallery/js/csshover.htc); }
</style>

<![endif]-->
<meta name="build" content="{{ site.siteName }}" />
</head>
{% if session.loggedIn == false %}
<body id="news" class="anonymous ">
{% else %}
<body id="news" class=" ">
{% endif %}
{% include "base/header.tpl" %}
   
<div id="content-container">

<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		{% if newsPage == "news" %}
		{% set articleLink = "articles" %}
		<ul>
			<li class="">
			<a href="{{ site.sitePath }}/community">Community</a>			</li>
    		<li class="selected">
				News    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/tag">Tags</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/events">Events</a>    		</li>
    		<li class="last">
				<a href="{{ site.sitePath }}/community/fansites">Fansites</a>    		</li>
    		<!-- <li class="last">
				<a href="{{ site.sitePath }}/events/steampunk">Steampunk</a>    		</li> 
			-->
		</ul>
		{% endif %}
		{% if newsPage == "events" %}
		{% set articleLink = "community/events" %}
		<ul>
			<li class="">
			<a href="{{ site.sitePath }}/community">Community</a>			</li>
    		<li class="">
				<a href="{{ site.sitePath }}/articles">News</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/tag">Tags</a>    		</li>
    		<li class="selected">
				Events			</li>
    		<li class="last">
				<a href="{{ site.sitePath }}/community/fansites">Fansites</a>    		</li>
    		<!-- <li class="last">
				<a href="{{ site.sitePath }}/events/steampunk">Steampunk</a>    		</li> 
			-->
		</ul>
		{% endif %}
		{% if newsPage == "fansites" %}
		{% set articleLink = "community/fansites" %}
		<ul>
			<li class="">
			<a href="{{ site.sitePath }}/community">Community</a>			</li>
    		<li class="">
				<a href="{{ site.sitePath }}/articles">News</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/tag">Tags</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/events">Events</a>    		</li>
    		<li class="selected last">
				Fansites			</li>
			<!-- 
    		<li class="selected">
				Fansites			</li>
    		<li class="last">
				<a href="{{ site.sitePath }}/events/steampunk">Steampunk</a>    		</li>
			-->
		</ul>
		{% endif %}
		
		<!-- 
		{% if newsPage == "custom_event" %}
		{% set articleLink = "articles" %}
		<ul>
			<li class="">
			<a href="{{ site.sitePath }}/community">Community</a>			</li>
    		<li class="">
				<a href="{{ site.sitePath }}/articles">News</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/tag">Tags</a>    		</li>
			<li class="">
				<a href="{{ site.sitePath }}/community/photos">Photos</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/events">Events</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/fansites">Fansites</a>    		</li>
    		<li class="selected last">
				Steampunk			</li>
		</ul>
		{% endif %}
		-->
    </div>
</div>

	
<div id="container">
	<div id="content" style="position: relative" class="clearfix">
		{% if newsPage == "custom_event" %}
		<div id="column1" class="column" style="width: 770px">
		{% else %}
		<div id="column1" class="column">     		
			<div class="habblet-container ">		
				<div class="cbb clearfix default ">
						{% if newsPage == "news" %}
						<h2 class="title">News</h2>
						{% endif %}
						{% if newsPage == "events" %}
						<h2 class="title">Events</h2>
						{% endif %}
						{% if newsPage == "fansites" %}
						<h2 class="title">Fansites</h2>
						{% endif %}
						<div id="article-archive">
						{% if monthlyView %}
							{% for month in months.entrySet() %}
								{% include "habblet/news_sidebar.tpl" with { "articles": month.getValue(), "header": month.getKey() } %}
							{% endfor %}
						{% elseif archiveView %}
							{% for archived in archives.entrySet() %}
								{% include "habblet/news_sidebar.tpl" with { "articles": archived.getValue(), "header": archived.getKey() } %}
							{% endfor %}
						{% else %}
							{% include "habblet/news_sidebar.tpl" with { "articles": articlesToday, "header": 'Today' } %}
							{% include "habblet/news_sidebar.tpl" with { "articles": articlesYesterday, "header": 'Yesterday' } %}
							{% include "habblet/news_sidebar.tpl" with { "articles": articlesThisWeek, "header": 'Last week' } %}
							{% include "habblet/news_sidebar.tpl" with { "articles": articlesThisMonth, "header": 'Last month' } %}
							{% include "habblet/news_sidebar.tpl" with { "articles": articlesPastYear, "header": 'This year' } %}
						{% endif %}
						{% if (not archiveView) %}
						<a href="{{ site.sitePath }}/{{ articleLink }}/archive">More news &raquo;</a>
						{% endif %}		
					</div>
				</div>
			</div>
			<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
		</div>
		<div id="column2" class="column">
		{% endif %}
			<div class="habblet-container ">		
				<div class="cbb clearfix notitle ">
					<div id="article-wrapper">
					    {% if "currentArticle" is present %}
						<h2>{{ currentArticle.title }}</h2>
						{% if newsPage != "custom_event" %}
						<div class="article-meta">Posted {{ currentArticle.getDate() }}
							<!-- <a href="{{ site.sitePath }}/{{ articleLink }}/category/{{ currentArticle.getCategoryLower() }}"> -->
							
							{% set num = 1 %}
							{% if (currentArticle.getCategories().length) > 0 %}
							{% for category in currentArticle.getCategories() %}				
								<a href="{{ site.sitePath }}/{{ articleLink }}/category/{{ category.getIndex() }}">{{ category.getLabel() }}</a>{% if num < (currentArticle.getCategories()|length) %},{% endif %}{% set num = num + 1 %}
							{% endfor %}
							{% endif %}			
							<!-- </a> -->
						</div>
						{% endif %}
												
						{% if currentArticle.articleImage != "" %}
						<img src="{{ currentArticle.articleImage }}" class="article-image"/>
						{% endif %}
						<p class="summary">{{ currentArticle.shortstory }}</p>
		
						<div class="article-body">
							<p>{{ currentArticle.getEscapedStory() }}</p>

							{% if newsPage != "custom_event" %}
							<div class="article-author">- {{ currentArticle.getAuthor() }}</div>
							{% endif %}
							
							<script type="text/javascript" language="Javascript">
								document.observe("dom:loaded", function() {
									$$('.article-images a').each(function(a) {
										Event.observe(a, 'click', function(e) {
											Event.stop(e);
											Overlay.lightbox(a.href, "Image is loading");
										});
									});
									
									$$('a.article-5').each(function(a) {
										a.replace(a.innerHTML);
									});
								});
							</script>
						</div>
						{% endif %}
					</div>
				</div>
			</div>
			<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
		</div>
<script type="text/javascript">
HabboView.run();
</script>
<script type="text/javascript">
HabboView.run();

</script>


<div id="column3" class="column">
				<div class="habblet-container ">
						<div class="ad-container">
						{% include "base/ads_container.tpl" %}
						</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
</div>

<!--[if lt IE 7]>
<script type="text/javascript">
Pngfix.doPngImageFix();
</script>
<![endif]-->

    </div>
{% include "base/footer.tpl" %}

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>