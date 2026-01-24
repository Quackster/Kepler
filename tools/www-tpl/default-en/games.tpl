<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }} ~ Games </title>

<script type="text/javascript">
var andSoItBegins = (new Date()).getTime();
</script>
    <link rel="shortcut icon" href="{{ site.staticContentPath }}/web-gallery/v2/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="alternate" type="application/rss+xml" title="Habbo: RSS" href="https://classichabbo.com/articles/rss.xml" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs2.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/visual.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/common.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/fullcontent.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs2.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/style.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/buttons.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/boxes.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/tooltips.css" type="text/css" />
<script src="/js/local/com.js" type="text/javascript"></script>


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

<style>
.habblet-text {
  margin-left: 10px;
  margin-right: 10px;
}
/* Create two equal columns that floats next to each other */
.column {
  float: left;
  width: 137px;
}

/* Clear floats after the columns */
.row:after {
  content: "";
  display: table;
  clear: both;
}
</style>

<meta name="description" content="Join the world's largest virtual hangout where you can meet and make friends. Design your own rooms, collect cool furniture, throw parties and so much more! Create your FREE Habbo today!" />
<meta name="keywords" content="habbo, virtual, world, join, groups, forums, play, games, online, friends, teens, collecting, social network, create, collect, connect, furniture, virtual, goods, sharing, badges, social, networking, hangout, safe, music, celebrity, celebrity visits, celebrities, room design, rares, rare furni, furni, free, avatar, online, teen, roleplaying, play, expression, mmo, mmorpg, massively multiplayer, chat" />

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
body { behavior: url(/js/csshover.htc); }
</style>
<![endif]-->
<meta name="build" content="21.0.53 - 20080403054049 - com" />
</head>
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
				Games
				
			</li>
    		<li class="">
				<a href="/groups/battleball_rebound">BattleBall: Rebound!</a>
    		</li>
    		<li class="">
				<a href="/groups/snow_storm">SnowStorm</a>
    		</li>
    		<li class="">
				<a href="/groups/wobble_squabble">Wobble Squabble</a>
    		</li>
    		<li class=" last">
				<a href="/groups/lido">Lido Diving</a>
    		</li>
	</ul>
    </div>
</div>

<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div id="column1" class="column">
				<div class="habblet-container ">		
						<div class="cbb clearfix green ">
	
							<h2 class="title">Recommended Games
							</h2>
						<div class="game-links-top">
<div><a href="/groups/battleball_rebound"><img src="{{ site.staticContentPath }}/web-gallery/v2/images/games/battleball.png" alt="BattleBall: Rebound! »" width="450" height="99" /></a></div>
<div><a href="/groups/snow_storm"><img src="{{ site.staticContentPath }}/web-gallery/v2/images/games/snowstorm.png" alt="SnowStorm »" width="450" height="99" /></a></div>
</div>

<ul class="game-links">
	<li><a href="/groups/battleball_rebound">BattleBall: Rebound! »</a></li>
	<li><a href="/groups/snow_storm">SnowStorm »</a></li>
	<li><a href="/groups/wobble_squabble">Wobble Squabble »</a></li>
	<li><a href="/groups/lido">Lido Diving »</a></li>
</ul>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 
				<div class="habblet-container ">		
						<div class="cbb clearfix orange ">
	
							<h2 class="title">High Scores
							</h2>
{% include "habblet/personalhighscores.tpl" %}

<script type="text/javascript">
new HighscoreHabblet("h116");
</script>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 
</div>
<div id="column2" class="column">
				<div class="habblet-container ">		
						<div class="cbb clearfix green ">
	
							<h2 class="title">Your Ticket To Excitement
							</h2>
						<div class="box-content">
	<div id="game-promo">
		Game tickets cost 1 Coin for 2, or you can purchase 20 Tickets for 6 Coins.
	</div>
</div>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
				
			<div class="habblet-container ">		
				<div class="cbb clearfix">
					<h2 class="title" style="background-color: lightblue">Scoring</h2>
					<div class="habblet-text">							</h2>
						<div class="row">
							<div class="column" style="width: 100px; margin-top:10px; margin-bottom:10px; margin-right:6px">
								<img class="credits-image" src="https://i.imgur.com/AOd2pRV.gif" alt="">
							</div>
							<div class="column" style="width: 170px;">
								{% if viewMonthlyScores %}
								<p style="margin-top: 10px">The following results are scores earned month to month.</p>
								<p>To view scores earned all time since game scoring has been collected, please click <a href="{{ site.sitePath }}/games/score_all_time">here</a>.</p>
								{% else %}
								<p style="margin-top: 10px">The following results are scores earned all time.</p>
								<p>To view scores earned month to month since game scoring has been collected, please click <a href="{{ site.sitePath }}/games">here</a>.</p>								
								{% endif %}
							</div>
						</div>
					</div>
				</div>
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