
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Credits </title>

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
				Coins			</li>
			<li class="">
				<a href="{{ site.sitePath }}/credits/club">{{ site.siteName }} Club</a>			</li>
			<li class="">
				<a href="{{ site.sitePath }}/credits/collectables">Collectables</a>			</li>
    		<li class=" last">
				<a href="{{ site.sitePath }}/credits/pixels">Pixels</a>    		</li>
		</ul>
    </div>
</div>
<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div id="column1" class="column">
			     		
				<div class="habblet-container ">		
						<div class="cbb clearfix green ">
	
							<h2 class="title">How to get Credits
							</h2>
							
						<script src="{{ site.staticContentPath }}/web-gallery/static/js/credits.js" type="text/javascript"></script>
<p class="credits-countries-select">
The good thing about this server is that credits a free, yes, free. You won't have to spend a thing to get credits for building your favourite rooms. Just find out by using the methods below to receive credits.
</p>
<ul id="credits-methods">
	<li id="credits-type-promo">
		<h4 class="credits-category-promo">Best Way</h4>
		<ul>
<li class="clearfix odd"><div id="method-3" class="credits-method-container">
					<div class="credits-summary" style="background-image: url({{ site.staticContentPath }}/c_images/album2705/logo_sms.png)">
						<h3>Be Online</h3>
						<p>Just by playing on the server daily you can receive coins!</p>
						
						<p class="credits-read-more" id="method-show-3" style="display: none">Read more</p>
					</div>
					<div id="method-full-3" class="credits-method-full">
							<p><b>Receive coins by being online</b><br/>You need to be in a room but every day, if you wait 5 minutes, you will recieve 120 credits just by being active.</p>
							<p>This happens once every 24 hours, so if you do the same thing tomorrow, you'll get another 120 credits!</p>
					</div>
					<script type="text/javascript">
					$("method-show-3").show();
					$("method-full-3").hide();
					</script>
				</div></li>
		</ul>
	</li>
	<li id="credits-type-quick_and_easy">
		<h4 class="credits-category-quick_and_easy">Other Ways</h4>
		<ul>
				
<li class="clearfix odd"><div id="method-1" class="credits-method-container">
					<div class="credits-summary" style="background-image: url({{ site.staticContentPath }}/c_images/album2705/payment_habbo_prepaid.png)">
						<h3>Vouchers</h3>
						<p>You can get special codes to redeem vouchers</p>
						
						<p class="credits-read-more" id="method-show-1" style="display: none">Read more</p>
					</div>
					<div id="method-full-1" class="credits-method-full">
							<p>Redeem your voucher code in your hotel purse, or on this page - and you will get your coins right away!</p>
					</div>
					<script type="text/javascript">
					$("method-show-1").show();
					$("method-full-1").hide();
					</script>
				</div></li>
		</ul>
	</li>
	 <li id="credits-type-other">
		<h4 class="credits-category-quick_and_easy">Other Ways</h4>
		<ul>
				
<li class="clearfix odd"><div id="method-2" class="credits-method-container">
					<div class="credits-summary" style="background-image: url({{ site.staticContentPath }}/c_images/album2705/byesw_hand.png)">
						<div class="credits-tools">
								<a  class="new-button" id="warn-clear-hand-button" href="#" onclick="warnClearHand()"><b>Reset Hand</b><i></i></a>
							
						</div>
						<h3>Reset Hand</h3>
						<p>Virtual hand too full of furniture? Click here to reset it.</p>
						
						<!-- <p class="credits-read-more" id="method-show-2" style="display: none">Read more</p>
					</div>
					<div id="method-full-2" class="credits-method-full">
							<p>Simply click the button to reset your hand.</p>
							<p><strong>Warning: </strong> This action cannot be undone.</p>
					</div> -->
					{% if session.loggedIn %}
					<script type="text/javascript">
					var responseName = "wiped-hand";
					var responseWarnName = "warn-wiped-hand";
						
						
					function clearHand() {
						const http = new XMLHttpRequest();
						http.open("GET", habboReqPath + "/habblet/ajax/clear_hand");
						http.send();
						
						var responseName = "wiped-hand";
						
						if (document.getElementById(responseName) == null) {
							document.getElementById('method-2').insertAdjacentHTML('afterbegin', '<div id="' + responseName + '" style="border-radius:5px; background: #3ba800; padding: 8px; color: #FFFFFF;">The hand has been reset.</br></div><br />');
							document.getElementById(responseWarnName).remove();
						}
					}
					
					function warnClearHand() {
						var responseName = "warn-wiped-hand";
						
						if (document.getElementById(responseWarnName) == null) {
							document.getElementById('method-2').insertAdjacentHTML('afterbegin', '<div id="' + responseWarnName + '" style="border-radius:5px; background: #f29400; padding: 8px; color: #FFFFFF;">Are you sure? <strong>This  cannot be undone!</strong><a class="new-button" id="confirm-clear-hand-button" href="#" onclick="clearHand()"><b>Yes, clear it now!</b><i></i></a><br /><br /></div><br />');
							document.getElementById("warn-clear-hand-button").remove();
						}
					}
					
					$("method-show-2").show();
					$("method-full-2").hide();
					</script>
					{% else %}
					<script type="text/javascript">
					var responseWarnName = "warn-wiped-hand";
					
					function warnClearHand() {
						var responseName = "warn-wiped-hand";
						
						if (document.getElementById(responseWarnName) == null) {
							document.getElementById('method-2').insertAdjacentHTML('afterbegin', '<div id="' + responseWarnName + '" style="border-radius:5px; background: red; padding: 8px; color: #FFFFFF;">You must be logged in to do this<br /></div><br />');
							document.getElementById("warn-clear-hand-button").remove();
						}
					}
					
					$("method-show-2").show();
					$("method-full-2").hide();
					</script>
					{% endif %}
				</div></li>
		</ul>
	</li> 
</ul>

<script type="text/javascript">
L10N.put("credits.navi.read_more", "Read more");
L10N.put("credits.navi.close_fulltext", "Close instructions");
PaymentMethodHabblet.init();
</script>
	
						
					</div>

				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

</div>
<div id="column2" class="column">
			     		
				<div class="habblet-container ">		
						<div class="cbb clearfix brown ">
	
							<h2 class="title">Your purse							</h2>
							{% if session.loggedIn == false %}
								<div class="box-content">You need to sign in to see the purse</div>
							{% else %}
					
		<div id="purse-habblet">
			<form method="post" action="{{ site.sitePath }}/credits" id="voucher-form">

			<ul>
			<li class="even icon-purse">
			<div>You Currently Have:</div>
			<span class="purse-balance-amount">{{ playerDetails.credits }} Coins</span>
			<div class="purse-tx"><a href="{{ site.sitePath }}/credits/history">Account transactions</a></div>
			</li>

			<li class="odd">

			<div class="box-content">
			<div>Enter voucher code (without spaces):</div>
			<input type="text" name="voucherCode" value="" id="purse-habblet-redeemcode-string" class="redeemcode" />
			<a href="#" id="purse-redeemcode-button" class="new-button purse-icon" style="float:left"><b><span></span>Enter</b><i></i></a>
			</div>
			</li>
			</ul>
			<div id="purse-redeem-result">
			</div>	</form>
		</div>
		{% endif %}

<script type="text/javascript">
	new PurseHabblet();
</script>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
				
				<div class="habblet-container ">		
						<div class="cbb clearfix orange ">
	
							<h2 class="title">What are {{ site.siteName }} Coins?							</h2>

						<div id="credits-promo" class="box-content credits-info">
    <div class="credit-info-text clearfix">
        <img class="credits-image" src="{{ site.staticContentPath }}/web-gallery/v2/images/credits/poor.png" alt="" width="77" height="105" />
        <p class="credits-text">{{ site.siteName }} Coins are the Hotel's currency. You can use them to buy all kinds of things, from rubber ducks and sofas, to VIP membership, jukeboxes and teleports.</p>
    </div>
    <p class="credits-text-2">All legitimate ways to get {{ site.siteName }} coins are to the left. Remember: {{ site.siteName }} coins are ALWAYS and always will be free.</p>
</div>
	
						
					</div>

				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 
				<div class="habblet-container ">		
						<div class="cbb clearfix blue ">
	
							<h2 class="title">Always Ask Permission First!
							</h2>
						<div id="credits-safety" class="box-content credits-info">
    <div class="credit-info-text clearfix"><img class="credits-image" src="{{ site.sitePath }}/web-gallery/v2/images/credits_permission.png" width="114" height="136"/><p class="credits-text">Always ask permission from your parent or guardian before you buy Habbo Coins. If you do not do this and the payment is later canceled or declined, you will be permanently banned.</p></div>
    <p class="credits-text-2">Uh-oh!</p>
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

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>