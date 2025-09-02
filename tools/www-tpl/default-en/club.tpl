
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Club </title>

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
var habboName = "{{ playerDetails.getName() }}";
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
var habboPartner = "";
window.name = "habboMain";
if (typeof HabboClient != "undefined") { HabboClient.windowName = "client"; }

</script>



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

{% if session.currentPage == "credits" %}
<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		<ul>
			<li class="">
				<a href="{{ site.sitePath }}/credits">Coins</a>			</li>
			<li class="selected">
				{{ site.siteName }} Club			</li>
			<li class="">
				<a href="{{ site.sitePath }}/credits/collectables">Collectables</a>			</li>
    		<li class=" last">
				<a href="{{ site.sitePath }}/credits/pixels">Pixels</a>    		</li>
		</ul>
    </div>
</div>
{% endif %}

{% if session.currentPage == "me" %}
<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		<ul>
			<li class="">
				<a href="{{ site.sitePath }}/me">Home</a>			</li>
    		<li class="">
				<a href="{{ site.sitePath }}/home/{{ playerDetails.getName() }}">My Page</a>    		</li>
			<li class="">
				<a href="{{ site.sitePath }}/profile">Account Settings</a>			</li>
				<li class="selected{% if gameConfig.getInteger('guides.group.id') == 0 %} last{% endif %}">
			<a href="{{ site.sitePath }}/club">{{ site.siteName }} Club</a>
				</li>
				{% if gameConfig.getInteger('guides.group.id') > 0 %}
				<li class=" last">
					<a href="{{ site.sitePath }}/groups/officialhabboguides">Habbo Guides</a>
				</li>
				{% endif %}
		</ul>
    </div>
</div>
{% endif %}

<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div id="column1" class="column">

				<div class="habblet-container ">		
						<div class="cbb clearfix hcred ">
	
							<h2 class="title">{{ site.siteName }} Club: become a VIP!							</h2>
						<div id ="habboclub-products">
    <div id="habboclub-clothes-container">
        <div class="habboclub-extra-image"></div>
        <div class="habboclub-clothes-image"></div>
    </div>

    <div class="clearfix"></div>
    <div id="habboclub-furniture-container">
        <div class="habboclub-furniture-image"></div>
    </div>
</div>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

			     		
				<div class="habblet-container ">		
						<div class="cbb clearfix lightbrown ">

	
							<h2 class="title">Benefits							</h2>
						<div id="habboclub-info" class="box-content">
    <p>{{ site.siteName }} Club is our VIP members-only club - absolutely no riff-raff admitted! Members enjoy a wide range of benefits, including exclusive clothes, free gifts and an extended Friends List. See below for all the sparkly, attractive reasons to join.</p>
    <h3 class="heading">1. Extra Clothes &amp; Accessories</h3>
    <p class="content habboclub-clothing">Show off your new status with a variety of extra clothes and accessories, along with special hairstyles and colors.        <br /><br /><a href="{{ site.sitePath }}/credits/club/tryout">Try out {{ site.siteName }} Club clothes for yourself!</a>

    </p>
    <h3 class="heading">2. Free Furni</h3>
    <p class="content habboclub-furni">Once a month, every month, you'll get an exclusive piece of {{ site.siteName }} Club furni.</p>        
    <p class="content">Important note: club time is cumulative. This means that if you have a break in membership, and then rejoin, you'll start back in the same place you left off.</p>
    <h3 class="heading">3. Exclusive Room Layouts</h3>
    <p class="content">Special Guest Room layouts, only for {{ site.siteName }} Club members. Perfect for showing off your new furni!</p>
    <p class="habboclub-room" />

    <h3 class="heading">4. Access All Areas</h3>
    <p class="content">Jump the annoying queues when rooms are loading. And that's not all - you'll also get access to HC-only Public Rooms.</p>
    <h3 class="heading">5. Homepage Upgrades</h3>
    <p class="content">Join {{ site.siteName }} Club and say goodbye to homepage ads! And this means you can make the most of the HC skins and backgrounds too.</p>
    <h3 class="heading">6. More Friends</h3>
    <p class="content habboclub-communicator">600 people! Now that's a lot of buddies however you look at it. More than you can poke with a medium-sized stick, or a big-sized small stick.</p>

    <h3 class="heading">7. Special Commands</h3>
    <p class="content habboclub-commands right">Use the :chooser command to see a clickable list of all the users in the room. Pretty handy when you want to sit next to your mate, or kick out a troublemaker.</p>
    <br />
    <p>Use the :furni command to list all the items in a room. Everything is listed, even those sneakily hidden items.</p>
</div>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

</div>
<div id="column2" class="column">
			     		
				<div class="habblet-container ">		
						<div class="cbb clearfix hcred ">
	
							<h2 class="title">My Membership							</h2>
							

						<script src="{{ site.staticContentPath }}/web-gallery/static/js/habboclub.js" type="text/javascript"></script>
						{% include "base/hc_status.tpl" %}				
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

			     		
				<div class="habblet-container ">		
						<div class="cbb clearfix lightbrown ">
	
							<h2 class="title">Monthly Gifts
							</h2>
						<script src="{{ site.staticContentPath }}/web-gallery/static/js/habboclub.js" type="text/javascript"></script>
<div id="hc-gift-catalog">
  {% include "habblet/habboclubgift.tpl" %}
</div>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

</div>
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