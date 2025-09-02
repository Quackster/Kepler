
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Community </title>

<script type="text/javascript">
var andSoItBegins = (new Date()).getTime();
</script>
    <link rel="shortcut icon" href="{{ site.staticContentPath }}/web-gallery/v2/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="alternate" type="application/rss+xml" title="{{ site.siteName }}: RSS" href="{{ site.sitePath }}/articles/rss.xml" />
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
var habboName = "{{ session.loggedIn ? playerDetails.getName() : "" }}";
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
var habboPartner = "";
window.name = "habboMain";
if (typeof HabboClient != "undefined") { HabboClient.windowName = "client"; }

</script>

<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/rooms.css" type="text/css" /> 
<script src="{{ site.staticContentPath }}/web-gallery/static/js/rooms.js" type="text/javascript"></script> 
<script src="{{ site.staticContentPath }}/web-gallery/static/js/moredata.js" type="text/javascript"></script> 


<meta name="description" content="Join the world's largest virtual hangout where you can meet and make friends. Design your own rooms, collect cool furniture, throw parties and so much more! Create your FREE {{ site.siteName }} today!" />
<meta name="keywords" content="{{ site.siteName }}, virtual, world, join, groups, forums, play, games, online, friends, teens, collecting, social network, create, collect, connect, furniture, virtual, goods, sharing, badges, social, networking, hangout, safe, music, celebrity, celebrity visits, cele" />

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

<meta name="build" content="HavanaWeb" />
</head>

{% if session.loggedIn == false %}
<body id="home" class="anonymous ">
{% else %}
<body id="home" class=" ">
{% endif %}

{% include "base/header.tpl" %}

<div id="content-container">

<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		<ul>
			<li class="selected">
				Community			</li>
    		<li class="">
				<a href="{{ site.sitePath }}/articles">News</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/tag">Tags</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/events">Events</a>    		</li>
    		<li class=" last">
				<a href="{{ site.sitePath }}/community/fansites">Fansites</a>    		</li>
    		<!-- <li class=" last">
				<a href="{{ site.sitePath }}/events/steampunk">Steampunk</a>    		</li> -->
		</ul>
    </div>
</div>

<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div id="column1" class="column">

				<div class="habblet-container ">		
						<div class="cbb clearfix green ">

<div class="box-tabs-container clearfix">
    <h2>Rooms</h2>
    <ul class="box-tabs">
        <li id="tab-0-0-1"><a href="#">Top Rated</a><span class="tab-spacer"></span></li>
        <li id="tab-0-0-2" class="selected"><a href="#">Recommended Rooms</a><span class="tab-spacer"></span></li>
    </ul>
</div>
    <div id="tab-0-0-1-content"  style="display: none">

    		<div class="progressbar"><img src="{{ site.staticContentPath }}/web-gallery/images/progress_bubbles.gif" alt="" width="29" height="6" /></div>
    		<a href="{{ site.sitePath }}/habblet/proxy?hid=h120" class="tab-ajax"></a>
    </div>
    <div id="tab-0-0-2-content" >

<div id="rooms-habblet-list-container-h119" class="recommendedrooms-lite-habblet-list-container">
        <ul class="habblet-list">
{% autoescape 'html' %}
{% set num = 0 %}
{% for room in recommendedRooms %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	
	{% set occupancyLevel = 0 %}
	{% if room.getData().getVisitorsNow() > 0 %}
	
	{% set percentage = ((room.getData().getVisitorsNow() * 100) / room.getData().getVisitorsMax()) %}
	
	{% if (percentage >= 99) %}
		{% set occupancyLevel = 5 %}
	{% elseif (percentage > 65) %}
		{% set occupancyLevel = 4 %}
	{% elseif (percentage > 32) %}
		{% set occupancyLevel = 3 %}
	{% elseif (percentage > 0) %}
		{% set occupancyLevel = 2 %}
	{% endif %}
	
	{% endif %}
	
    <span class="clearfix enter-room-link room-occupancy-{{ occupancyLevel }}" title="Go to room" roomid="{{ room.getData().getId() }}">
	    <span class="room-enter">Enter {{ site.siteName }} Hotel</span>
	    <span class="room-name">{% autoescape 'html' %}{{ room.getData().getName() }}{% endautoescape %}</span>
	    <span class="room-description">{% autoescape 'html' %}{{ room.getData().getDescription() }}{% endautoescape %}</span>
		<span class="room-owner">Owner: <a href="{{ site.sitePath }}/home/{{ room.getData().getOwnerName() }}">{{ room.getData().getOwnerName() }}</a></span>
    </span>
</li>
{% set num = num + 1 %}
{% endfor %}
        </ul>
            <div id="room-more-data-h119" style="display: none">
                <ul class="habblet-list room-more-data">

{% for room in hiddenRecommendedRooms %}
	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	
	{% set occupancyLevel = 0 %}
	{% if room.getData().getVisitorsNow() > 0 %}
	
	{% set percentage = ((room.getData().getVisitorsNow() / room.getData().getVisitorsMax()) * 100) %}
	
	{% if (percentage >= 99) %}
		{% set occupancyLevel = 5 %}
	{% elseif (percentage > 65) %}
		{% set occupancyLevel = 4 %}
	{% elseif (percentage > 32) %}
		{% set occupancyLevel = 3 %}
	{% elseif (percentage > 0) %}
		{% set occupancyLevel = 2 %}
	{% endif %}
	
	{% endif %}
	
    <span class="clearfix enter-room-link room-occupancy-{{ occupancyLevel }}" title="Go to room" roomid="{{ room.getData().getId() }}">
	    <span class="room-enter">Enter {{ site.siteName }} Hotel</span>
	    <span class="room-name">{% autoescape 'html' %}{{ room.getData().getName() }}{% endautoescape %}</span>
	    <span class="room-description">{% autoescape 'html' %}{{ room.getData().getDescription() }}{% endautoescape %}</span>
		<span class="room-owner">Owner: <a href="{{ site.sitePath }}/home/{{ room.getData().getOwnerName() }}">{{ room.getData().getOwnerName() }}</a></span>
    </span>
</li>

{% set num = num + 1 %}
{% endfor %}
{% endautoescape %}
                </ul>
            </div>
            <div class="clearfix">
                <a href="#" class="room-toggle-more-data" id="room-toggle-more-data-h119">Show more rooms</a>
            </div>
</div>
<script type="text/javascript">
L10N.put("show.more", "Show more rooms");
L10N.put("show.less", "Show fewer rooms");
var roomListHabblet_h119 = new RoomListHabblet("rooms-habblet-list-container-h119", "room-toggle-more-data-h119", "room-more-data-h119");
</script>    </div>

					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
				
				<div class="habblet-container ">		
						<div class="cbb clearfix blue ">
<div class="box-tabs-container clearfix">
    <h2>Groups</h2>
    <ul class="box-tabs">
        <li id="tab-0-1-1"><a href="#">Hot Groups</a><span class="tab-spacer"></span></li>
        <li id="tab-0-1-2" class="selected"><a href="#">Recent topics</a><span class="tab-spacer"></span></li>
    </ul>

</div>
    <div id="tab-0-1-1-content"  style="display: none">
    		<div class="progressbar"><img src="{{ site.staticContentPath }}/web-gallery/images/progress_bubbles.gif" alt="" width="29" height="6" /></div>
    		<a href="{{ site.sitePath }}/habblet/proxy?hid=h122" class="tab-ajax"></a>
    </div>
    <div id="tab-0-1-2-content" >

<ul class="active-discussions-toplist">
	{% autoescape 'html' %}
	{% set num = 1 %}
	{% for topic in recentTopics %}

	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	
	<a href="{{ site.sitePath }}/groups/{{ topic.getGroupId() }}/id/discussions/{{ topic.getId() }}/id" class="topic">
		<span>{{ topic.getTopicTitle }}</span>

	</a>
	<div class="topic-info post-icon">
		<span class="grey">(</span>
			<a href="{{ site.sitePath }}/groups/{{ topic.getGroupId() }}/id/discussions/{{ topic.getId() }}/id/page/1" class="topiclist-page-link secondary">1</a>
			{% if topic.getRecentPages()|length > 0 %}
				...
			{% for page in topic.getRecentPages() %}
			<a href="{{ site.sitePath }}/groups/{{ topic.getGroupId() }}/id/discussions/{{ topic.getId() }}/id/page/{{ page }}" class="topiclist-page-link secondary">{{ page }}</a>
			{% endfor %}
			{% endif %}
		<span class="grey">)</span>
	 </div>
</li>
	{% set num = num + 1 %}
	{% endfor %}
	{% endautoescape %}
</ul>

<div id="active-discussions-toplist-hidden-h121" style="display: none">
    <ul class="active-discussions-toplist">
	{% autoescape 'html' %}
	{% set num = 1 %}
	{% for topic in recentHiddenTopics %}

	{% if num % 2 == 0 %}
	<li class="even">
	{% else %}
	<li class="odd">
	{% endif %}
	
	<a href="{{ site.sitePath }}/groups/{{ topic.getGroupId() }}/id/discussions/{{ topic.getId() }}/id" class="topic">
		<span>{{ topic.getTopicTitle }}</span>

	</a>
	<div class="topic-info post-icon">
		<span class="grey">(</span>
			<a href="{{ site.sitePath }}/groups/{{ topic.getGroupId() }}/id/discussions/{{ topic.getId() }}/id/page/1" class="topiclist-page-link secondary">1</a>
			{% if topic.getRecentPages()|length > 0 %}
				...
			{% for page in topic.getRecentPages() %}
			<a href="{{ site.sitePath }}/groups/{{ topic.getGroupId() }}/id/discussions/{{ topic.getId() }}/id/page/{{ page }}" class="topiclist-page-link secondary">{{ page }}</a>
			{% endfor %}
			{% endif %}
		<span class="grey">)</span>
	 </div>
</li>
	{% set num = num + 1 %}
	{% endfor %}
	{% endautoescape %}
</ul>

</div>
<div class="clearfix">
    <a href="#" class="discussions-toggle-more-data secondary" id="discussions-toggle-more-data-h121">Show more discussions</a>
</div>
<script type="text/javascript">
L10N.put("show.more.discussions", "Show more discussions");
L10N.put("show.less.discussions", "Show less discussions");
var discussionMoreDataHelper = new MoreDataHelper("discussions-toggle-more-data-h121", "active-discussions-toplist-hidden-h121","discussions");
</script>
    </div>

					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
				
				<div class="habblet-container ">		
						<div class="cbb clearfix activehomes ">
	
							<h2 class="title">Random {{ site.siteName }}s - Click Us!							</h2>
						<div id="homes-habblet-list-container" class="habblet-list-container">
	<img class="active-habbo-imagemap" src="{{ site.staticContentPath }}/web-gallery/v2/images/activehomes/transparent_area.gif" width="435px" height="230px" usemap="#habbomap" />

{% set num = 0 %}
{% for habbo in randomHabbos %}
        <div id="active-habbo-data-{{ num }}" class="active-habbo-data">
                    <div class="active-habbo-data-container">
						{% if habbo.isOnline() %}
						<div class="active-name online">{{ habbo.getName() }}</div>
						{% else %}
						<div class="active-name offline">{{ habbo.getName() }}</div>
						{% endif %}
						
                        {{ site.siteName }} created on: {{ habbo.getCreatedAt() }}
												{% autoescape 'html' %}
                            <p class="motto">{{ habbo.getMotto() }}</p>
												{% endautoescape %}
                    </div>
                </div>
                <input type="hidden" id="active-habbo-url-{{ num }}" value="{{ site.sitePath }}/home/{{ habbo.getName() }}"/>
                <input type="hidden" id="active-habbo-image-{{ num }}" class="active-habbo-image" value="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ habbo.getFigure() }}&size=b&direction=4&head_direction=4&crr=0&gesture=sml&frame=1
" />
{% set num = num + 1 %}
{% endfor %}
            <div id="placeholder-container">
                    <div id="active-habbo-image-placeholder-0" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-1" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-2" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-3" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-4" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-5" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-6" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-7" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-8" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-9" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-10" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-11" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-12" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-13" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-14" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-15" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-16" class="active-habbo-image-placeholder"></div>
                    <div id="active-habbo-image-placeholder-17" class="active-habbo-image-placeholder"></div>
            </div>
    </div>

    <map id="habbomap" name="habbomap">
            <area id="imagemap-area-0" shape="rect" coords="55,53,95,103" href="#" alt=""/>
            <area id="imagemap-area-1" shape="rect" coords="120,53,160,103" href="#" alt=""/>
            <area id="imagemap-area-2" shape="rect" coords="185,53,225,103" href="#" alt=""/>
            <area id="imagemap-area-3" shape="rect" coords="250,53,290,103" href="#" alt=""/>
            <area id="imagemap-area-4" shape="rect" coords="315,53,355,103" href="#" alt=""/>
            <area id="imagemap-area-5" shape="rect" coords="380,53,420,103" href="#" alt=""/>
            <area id="imagemap-area-6" shape="rect" coords="28,103,68,153" href="#" alt=""/>
            <area id="imagemap-area-7" shape="rect" coords="93,103,133,153" href="#" alt=""/>
            <area id="imagemap-area-8" shape="rect" coords="158,103,198,153" href="#" alt=""/>
            <area id="imagemap-area-9" shape="rect" coords="223,103,263,153" href="#" alt=""/>
            <area id="imagemap-area-10" shape="rect" coords="288,103,328,153" href="#" alt=""/>
            <area id="imagemap-area-11" shape="rect" coords="353,103,393,153" href="#" alt=""/>
            <area id="imagemap-area-12" shape="rect" coords="55,153,95,203" href="#" alt=""/>
            <area id="imagemap-area-13" shape="rect" coords="120,153,160,203" href="#" alt=""/>
            <area id="imagemap-area-14" shape="rect" coords="185,153,225,203" href="#" alt=""/>
            <area id="imagemap-area-15" shape="rect" coords="250,153,290,203" href="#" alt=""/>
            <area id="imagemap-area-16" shape="rect" coords="315,153,355,203" href="#" alt=""/>
            <area id="imagemap-area-17" shape="rect" coords="380,153,420,203" href="#" alt=""/>
    </map>
<script type="text/javascript">
    var activeHabbosHabblet = new ActiveHabbosHabblet();
    document.observe("dom:loaded", function() { activeHabbosHabblet.generateRandomImages(); });
</script>


					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

</div>
<div id="column2" class="column">

				<div class="habblet-container news-promo">
						<div class="cbb clearfix notitle ">
						<div id="newspromo">
        <div id="topstories">
	        <div class="topstory" style="background-image: url({{ article1.getLiveTopStory() }})">
	            <h4>Latest news </a></h4>
	            <h3><a href="{{ site.sitePath }}/articles/{{ article1.getUrl() }}">{% if article1.isPublished() == false %}*{% endif %}{{ article1.title }}</a></h3>
	            <p class="summary">
	            {{ article1.shortstory }}	            </p>
	            <p>
	                <a href="{{ site.sitePath }}/articles/{{ article1.getUrl() }}">Read more &raquo;</a>
	            </p>
	        </div>
	        <div class="topstory" style="background-image: url({{ article2.getLiveTopStory() }}); display: none">
	            <h4>Latest news</a></h4>
	            <h3><a href="{{ site.sitePath }}/articles/{{ article2.getUrl() }}">{% if article2.isPublished() == false %}*{% endif %}{{ article2.title }}</a></h3>
	            <p class="summary">
	            {{ article2.shortstory }}	            </p>
	            <p>
	                <a href="{{ site.sitePath }}/articles/{{ article2.getUrl() }}">Read more &raquo;</a>
	            </p>
	        </div>
	        <div class="topstory" style="background-image: url({{ article3.getLiveTopStory() }}); display: none">
	            <h4>Latest news</a></h4>
	            <h3><a href="{{ site.sitePath }}/articles/{{ article3.getUrl() }}">{% if article3.isPublished() == false %}*{% endif %}{{ article3.title }}</a></h3>
	            <p class="summary">
	            {{ article3.shortstory }}	            </p>
	            <p>
	                <a href="{{ site.sitePath }}/articles/{{ article3.getUrl() }}">Read more &raquo;</a>
	            </p>
	        </div>
            <div id="topstories-nav" style="display: none"><a href="#" class="prev">&laquo; Previous</a><span>1</span> / 3<a href="#" class="next">Next &raquo</a></div>
        </div>
        <ul class="widelist">
            <li class="even">
                <a href="{{ site.sitePath }}/articles/{{ article4.getUrl() }}">{{ article4.title }}</a><div class="newsitem-date">{{ article4.getDate() }}</div>
            </li>
            <li class="odd">
                <a href="{{ site.sitePath }}/articles/{{ article5.getUrl() }}">{{ article5.title }}</a><div class="newsitem-date">{{ article5.getDate() }}</div>
            </li>
            <li class="last"><a href="{{ site.sitePath }}/articles">More news &raquo;</a></li>
        </ul>
</div>
<script type="text/javascript">
	document.observe("dom:loaded", function() { NewsPromo.init(); });
</script>
					</div>

				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

				<div class="habblet-container ">		
					<div class="cbb clearfix green ">
						<h2 class="title">Tags							</h2>
						{% include "habblet/tagList.tpl" %}
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
				<div>
				<p><iframe src="https://discordapp.com/widget?id=524768066907668521&theme=light" height="280" allowtransparency="true" frameborder="0"></iframe></p>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

</div>

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


</body>
</html>