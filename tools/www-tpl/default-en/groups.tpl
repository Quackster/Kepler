
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Group Home: {% autoescape 'html' %}{{ group.getName }}{% endautoescape %} </title>

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

<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/myhabbo.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/skins.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/dialogs.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/buttons.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/control.textarea.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/boxes.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/myhabbo.css" type="text/css" />
	<link href="{{ site.staticContentPath }}/web-gallery/styles/myhabbo/assets.css" type="text/css" rel="stylesheet" />

<script src="{{ site.staticContentPath }}/web-gallery/static/js/homeview.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/lightwindow.css" type="text/css" />

<script src="{{ site.staticContentPath }}/web-gallery/static/js/homeauth.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/group.css" type="text/css" />

<!-- HTML5 trax player -->
<script src="{{ site.staticContentPath }}/web-gallery/static/js/webbanditten/traxplayer.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/webbanditten/traxplayer.css" type="text/css" />

<style type="text/css">
    #playground, #playground-outer {
	    width: 922px;
	    height: 1360px;
    }
</style>

{% if editMode %}
<script src="{{ site.staticContentPath }}/web-gallery/static/js/homeedit.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript">
document.observe("dom:loaded", function() { initView(1, 1); });
function isElementLimitReached() {
	if (getElementCount() >= {{ stickerLimit }}) {
		showHabboHomeMessageBox("Error", "You have already reached the maximum number of elements on the page. Remove a sticker, note or widget to be able to place this item.", "Close");
		return true;
	}
	return false;
}

function cancelEditing(expired) {
	location.replace("{{ site.sitePath }}/groups/actions/cancelEditingSession" + (expired ? "?expired=true" : ""));
}

function getSaveEditingActionName(){
	return '/groups/actions/saveEditingSession';
}

function showEditErrorDialog() {
	var closeEditErrorDialog = function(e) { if (e) { Event.stop(e); } Element.remove($("myhabbo-error")); Overlay.hide(); }
	var dialog = Dialog.createDialog("myhabbo-error", "", false, false, false, closeEditErrorDialog);
	Dialog.setDialogBody(dialog, '<p>Error occurred! Please try again in couple of minutes.</p><p><a href="#" class="new-button" id="myhabbo-error-close"><b>Close</b><i></i></a></p><div class="clear"></div>');
	Event.observe($("myhabbo-error-close"), "click", closeEditErrorDialog);
	Dialog.moveDialogToCenter(dialog);
	Dialog.makeDialogDraggable(dialog);
}

	document.observe("dom:loaded", function() { 
		Dialog.showInfoDialog("session-start-info-dialog", 
		"Your editing session will time out in {{ expireMinutes }} minutes.", 
		"Ok", function(e) {Event.stop(e); Element.hide($("session-start-info-dialog"));Overlay.hide();Utils.setAllEmbededObjectsVisibility('hidden');});
		var timeToTwoMinuteWarning= 1676000;
		if(timeToTwoMinuteWarning > 0){
			setTimeout(function(){ 
				Dialog.showInfoDialog("session-ends-warning-dialog",
					"Your editing session will time out in 2 minutes.", 
					"Ok", function(e) {Event.stop(e); Element.hide($("session-ends-warning-dialog"));Overlay.hide();Utils.setAllEmbededObjectsVisibility('hidden');});
			}, timeToTwoMinuteWarning);
		}
	});

function showSaveOverlay() {
	var invalidPos = getElementsInInvalidPositions();
	if (invalidPos.length > 0) {
	    $A(invalidPos).each(function(el) { Element.scrollTo(el);  Effect.Pulsate(el); });
	    showHabboHomeMessageBox("Whoops! You can\'t do that!", "Sorry, but you can\'t place your stickers, notes or widgets here. Close the window to continue editing your page.", "Close");
		return false;
	} else {
		Overlay.show(null,'Saving');
		return true;
	}
}
</script>
{% else %}
<script type="text/javascript">
document.observe("dom:loaded", function() { initView({{ group.id }}, {% if session.loggedIn == false %}-1{% else %}{{ playerDetails.id }}{% endif %}); });
</script>
{% endif %}

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
<body id="viewmode" class=" anonymous ">
{% else %}
	{% if editMode %}
	<body id="editmode" class=" ">
	{% else %}
	<body id="viewmode" class=" ">
	{% endif %}
{% endif %}
{% include "base/header.tpl" %}

<div id="content-container">

{% if session.currentPage == "games" %}
<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
	<ul>
			<li class="">
				<a href="/games">Games</a>
			</li>
    		<li class="{% if group.getAlias() == 'battleball_rebound' %}selected{% endif %}">
				<a href="/groups/battleball_rebound">BattleBall: Rebound!</a>
    		</li>
    		<li class="{% if group.getAlias() == 'snow_storm' %}selected{% endif %}">
				<a href="/groups/snow_storm">SnowStorm</a>
    		</li>
    		<li class="{% if group.getAlias() == 'wobble_squabble' %}selected{% endif %}">
				<a href="/groups/wobble_squabble">Wobble Squabble</a>
    		</li>
    		<li class="{% if group.getAlias() == 'lido' %}selected{% endif %} last">
				<a href="/groups/lido">Lido Diving</a>
    		</li>
	</ul>
    </div>
</div>
{% endif %}

{% if session.currentPage == "community" %}
<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		<ul>
    		<li class="">
				<a href="{{ site.sitePath }}/community">Community</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/articles">News</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/tag">Tags</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/photos">Photos</a>    		</li>
    		<li class="">
				<a href="{{ site.sitePath }}/community/events">Events</a>    		</li>
    		<li class=" last">
				<a href="{{ site.sitePath }}/community/fansites">Fansites</a>    		</li>
		</ul>
    </div>
</div>
{% endif %}

<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div id="mypage-wrapper" class="cbb blue">
<div class="box-tabs-container box-tabs-left clearfix">
	{% if editMode %}

	{% else %}
		{% if (session.loggedIn) and (group.hasAdministrator(playerDetails.getId())) %}
		<a href="#" id="myhabbo-group-tools-button" class="new-button dark-button edit-icon" style="float:left"><b><span></span>Edit</b><i></i></a>	
		{% endif %}
		<div class="myhabbo-view-tools">
		{% if session.loggedIn == false %}
		<a href="#" id="reporting-button" style="display: none;">Show report buttons</a>
		{% else %}
			{% if (group.isPendingMember(playerDetails.getId()) == false) %}
				{% if (group.isMember(playerDetails.getId()) == false) %}
					{% if group.getGroupType() == 0 or group.getGroupType() == 3 %}
					<a href="{{ site.sitePath }}/groups/actions/join?groupId=101" id="join-group-button">Join</a>
					{% elseif group.getGroupType() == 1 %}
					<a href="{{ site.sitePath }}/groups/actions/join?groupId=101" id="join-group-button">Request membership</a>	
					{% endif %}
					<a href="#" id="reporting-button" style="display: none;">Show report buttons</a>
				{% else %}
					{% if group.getOwnerId() != playerDetails.getId() %}
					<a href="{{ site.sitePath }}/groups/actions/leave?groupId=101" id="leave-group-button">Leave group</a>
					{% endif %}
					
					{% if groupMember.isFavourite(group.id) %}
					<a href="#" id="deselect-favorite-button">Remove favorite</a>
					{% else %}
					<a href="#" id="select-favorite-button">Make favorite</a>
					{% endif %}
				{% endif %}
			{% endif %}
		{% endif %}
	</div>
	{% endif %}

    <h2 class="page-owner">
		{% autoescape 'html' %}
    	{{ group.getName }} 
		{% endautoescape %}
		{% if group.getGroupType() == 1 %}<img src="{{ site.staticContentPath }}/web-gallery/images/groups/status_exclusive_big.gif" width="18" height="16" alt="Exclusive group" title="Exclusive group" class="header-bar-group-status" />{% elseif group.getGroupType() == 2%}<img src="{{ site.staticContentPath }}/web-gallery/images/groups/status_closed_big.gif" width="18" height="16" alt="myhabbo.headerbar.closed_group" title="myhabbo.headerbar.closed_group" class="header-bar-group-status" />{% endif %}   				    </h2>
    <ul class="box-tabs">
        <li class="selected"><a href="{{ group.generateClickLink() }}">Front Page</a><span class="tab-spacer"></span></li>
        <li><a href="{{ group.generateClickLink() }}/discussions">Discussion Forum {% if ((group.getForumType().getId() == 1) or (group.getForumPermission().getId() >= 1)) %}<img src="{{ site.staticContentPath }}/web-gallery/images/grouptabs/privatekey.png" title="Private Forum" alt="Private Forum" />{% endif %}</a><span class="tab-spacer"></span></li>
    </ul>
</div>
	<div id="mypage-content">
		{% if editMode %}

<div id="top-toolbar" class="clearfix">
	<ul>
		<li><a href="#" id="inventory-button">Inventory</a></li>
		<li><a href="#" id="webstore-button">Web Store</a></li>
	</ul>
	
	<form action="#" method="get" style="width: 50%">
		<a id="cancel-button" class="new-button red-button cancel-icon" href="#"><b><span></span>Cancel editing</b><i></i></a>
		<a id="save-button" class="new-button green-button save-icon" href="#"><b><span></span>Save changes</b><i></i></a>
	</form>
</div>

		{% endif %}
			<div id="mypage-bg" class="b_{{ group.getBackground() }}">
				{% if editMode %}
				<div id="playground-outer">				<div id="playground">
				{% else %}
				<div id="playground">
				{% endif %}

				{% for sticker in stickers %}
					{% if sticker.getProduct().data == "groupinfowidget" %}
						{% include "homes/widget/group_info_widget.tpl" with {"sticker": sticker} %}
					{% elseif sticker.getProduct().data == "guestbookwidget" %}
						{% include "homes/widget/guestbook_widget.tpl" with {"sticker": sticker} %}
					{% elseif sticker.getProduct().data == "stickienote" %}
						{% include "homes/widget/note.tpl" with {"sticker": sticker} %}
					{% elseif sticker.getProduct().data == "memberwidget" %}
						{% include "homes/widget/member_widget.tpl" with {"sticker": sticker} %}
					{% elseif sticker.getProduct().data == "traxplayerwidget" %}
						{% include "homes/widget/trax_player_widget.tpl" with {"sticker": sticker} %}
					{% else %}
						{% include "homes/widget/sticker.tpl" with {"sticker": sticker} %}
					{% endif %}
				{% endfor %}

				</div>		
				{% if editMode %}
				</div>				<div id="mypage-ad">
				{% else %}
				<div id="mypage-ad">
				{% endif %}
							
				</div>
			</div>
	</div>
</div>

{% if editMode %}

<script language="JavaScript" type="text/javascript">
initEditToolbar();
initMovableItems();
document.observe("dom:loaded", initDraggableDialogs);
Utils.setAllEmbededObjectsVisibility('hidden');
</script>

<div id="edit-save" style="display:none;"></div>    </div>

{% include "base/footer.tpl" %}


<div id="edit-menu" class="menu">
	<div class="menu-header">
		<div class="menu-exit" id="edit-menu-exit"><img src="{{ site.staticContentPath }}/web-gallery/images/dialogs/menu-exit.gif" alt="" width="11" height="11" /></div>
		<h3>Edit</h3>

	</div>
	<div class="menu-body">
		<div class="menu-content">
			<form action="#" onsubmit="return false;">
				<div id="edit-menu-skins">
	<select id="edit-menu-skins-select">
				<option value="1" id="edit-menu-skins-select-defaultskin">Default</option>
			<option value="6" id="edit-menu-skins-select-goldenskin">Golden</option>
			{% if playerDetails.getRank().getRankId() >= 5 %}
			<option value="9" id="edit-menu-skins-select-default">No Skin</option>	
			{% endif %}		
			<option value="3" id="edit-menu-skins-select-metalskin">Metal</option>
			<option value="5" id="edit-menu-skins-select-notepadskin">Notepad</option>
			<option value="2" id="edit-menu-skins-select-speechbubbleskin">Speech Bubble</option>
			<option value="4" id="edit-menu-skins-select-noteitskin">Stickie Note</option>
			{% if playerDetails.hasClubSubscription() %}
			<option value="8" id="edit-menu-skins-select-hc_pillowskin">HC Bling</option>
			<option value="7" id="edit-menu-skins-select-hc_machineskin">HC Scifi</option>
			{% endif %}
	</select>
				</div>
				<div id="edit-menu-stickie">

					<p>Warning! If you click 'Remove', the note will be permanently deleted.</p>
				</div>
				<div id="rating-edit-menu">
					<input type="button" id="ratings-reset-link" 
						value="Reset rating" />
				</div>
				<div id="highscorelist-edit-menu" style="display:none">
					<select id="highscorelist-game">
						<option value="">Select game</option>
												<option value="1">Battle Ball: Rebound!</option>
						<option value="2">SnowStorm</option>
						<option value="0">Wobble Squabble</option>
					</select>
				</div>
				<div id="edit-menu-remove-group-warning">
					<p>This item belongs to another user. If you remove it, it will return to their inventory.</p>

				</div>
				<div id="edit-menu-gb-availability">
					<select id="guestbook-privacy-options">
						<option value="private"{% if guestbookSetting == "private" %} selected{% endif %}>Members only</option>
						<option value="public"{% if guestbookSetting == "public" %} selected{% endif %}>Public</option>
					</select>
				</div>
				<div id="edit-menu-trax-select">

					<select id="trax-select-options"></select>
				</div>
				<div id="edit-menu-remove">
					<input type="button" id="edit-menu-remove-button" value="Remove" />
				</div>
			</form>
			<div class="clear"></div>
		</div>
	</div>

	<div class="menu-bottom"></div>
</div>
<script language="JavaScript" type="text/javascript">
Event.observe(window, "resize", function() { if (editMenuOpen) closeEditMenu(); }, false);
Event.observe(document, "click", function() { if (editMenuOpen) closeEditMenu(); }, false);
Event.observe("edit-menu", "click", Event.stop, false);
Event.observe("edit-menu-exit", "click", function() { closeEditMenu(); }, false);
Event.observe("edit-menu-remove-button", "click", handleEditRemove, false);
Event.observe("edit-menu-skins-select", "click", Event.stop, false);
Event.observe("edit-menu-skins-select", "change", handleEditSkinChange, false);
Event.observe("guestbook-privacy-options", "click", Event.stop, false);
Event.observe("guestbook-privacy-options", "change", handleGuestbookPrivacySettings, false);
Event.observe("trax-select-options", "click", Event.stop, false);
Event.observe("trax-select-options", "change", handleTraxplayerTrackChange, false);
</script>


<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>

{% else %}

<script type="text/javascript">
	Event.observe(window, "load", observeAnim);
	document.observe("dom:loaded", function() {
		initDraggableDialogs();
	});
</script>

    </div>
{% include "base/footer.tpl" %}
</div>

</div>


<div class="cbb topdialog" id="guestbook-form-dialog">
	<h2 class="title dialog-handle">Edit Guestbook entry</h2>
	
	<a class="topdialog-exit" href="#" id="guestbook-form-dialog-exit">X</a>
	<div class="topdialog-body" id="guestbook-form-dialog-body">

<div id="guestbook-form-tab">
<form method="post" id="guestbook-form">
    <p>
        Note: the message length must not exceed 200 characters        <input type="hidden" name="ownerId" value="1" />
	</p>
	<div>
	    <textarea cols="15" rows="5" name="message" id="guestbook-message"></textarea>
    <script type="text/javascript">
        bbcodeToolbar = new Control.TextArea.ToolBar.BBCode("guestbook-message");
        bbcodeToolbar.toolbar.toolbar.id = "bbcode_toolbar";
		        var colors = { "red" : ["#d80000", "Red"],
            "orange" : ["#fe6301", "Orange"],
            "yellow" : ["#ffce00", "Yellow"],
            "green" : ["#6cc800", "Green"],
            "cyan" : ["#00c6c4", "Cyan"],
            "blue" : ["#0070d7", "Blue"],
            "gray" : ["#828282", "Gray"],
            "black" : ["#000000", "Black"]
        };
        bbcodeToolbar.addColorSelect("Color", colors, true);
    </script>

<div id="linktool">
    <div id="linktool-scope">
        <label for="linktool-query-input">Create link:</label>
        <input type="radio" name="scope" class="linktool-scope" value="1" checked="checked"/>Habbos        <input type="radio" name="scope" class="linktool-scope" value="2"/>Rooms        <input type="radio" name="scope" class="linktool-scope" value="3"/>Groups    </div>
    <input id="linktool-query" type="text" name="query" value=""/>
    <a href="#" class="new-button" id="linktool-find"><b>Find</b><i></i></a>
    <div class="clear" style="height: 0;"><!-- --></div>

    <div id="linktool-results" style="display: none">
    </div>
    <script type="text/javascript">
        linkTool = new LinkTool(bbcodeToolbar.textarea);
    </script>
</div>
    </div>

	<div class="guestbook-toolbar clearfix">
		<a href="#" class="new-button" id="guestbook-form-cancel"><b>Cancel</b><i></i></a>
		<a href="#" class="new-button" id="guestbook-form-preview"><b>Preview</b><i></i></a>	
	</div>

</form>
</div>
<div id="guestbook-preview-tab">&nbsp;</div>
	</div>
</div>	
<div class="cbb topdialog" id="guestbook-delete-dialog">
	<h2 class="title dialog-handle">Delete entry</h2>
	
	<a class="topdialog-exit" href="#" id="guestbook-delete-dialog-exit">X</a>
	<div class="topdialog-body" id="guestbook-delete-dialog-body">
<form method="post" id="guestbook-delete-form">
	<input type="hidden" name="entryId" id="guestbook-delete-id" value="" />

	<p>Are you sure you want to delete this entry?</p>
	<p>
		<a href="#" id="guestbook-delete-cancel" class="new-button"><b>Cancel</b><i></i></a>
		<a href="#" id="guestbook-delete" class="new-button"><b>Delete</b><i></i></a>
	</p>
</form>
	</div>
</div>
<div id="group-tools" class="bottom-bubble">
	<div class="bottom-bubble-t"><div></div></div>
	<div class="bottom-bubble-c">
<h3>Edit group</h3>

<ul>
	{% if (hasMember and groupMember.getMemberRank().getRankId() >= 2) %}
	<li><a href="{{ site.sitePath }}/groups/actions/startEditingSession/{{ group.id }}" id="group-tools-style">Modify page</a></li>
	{% endif %}
	{% if (hasMember and groupMember.getMemberRank().getRankId() == 3) %}
	<li><a href="#" id="group-tools-settings">Settings</a></li>	<li><a href="#" id="group-tools-badge">Badge</a></li>
	{% endif %}
	{% if (group.getGroupType() != 3) and (hasMember and groupMember.getMemberRank().getRankId() >= 2) %}
	<li><a href="#" id="group-tools-members">Members</a></li>
	{% endif %}
</ul>

	</div>
	<div class="bottom-bubble-b"><div></div></div>
</div>

<div class="cbb topdialog black" id="dialog-group-settings">
	
	<div class="box-tabs-container">
<ul class="box-tabs">
	<li class="selected" id="group-settings-link-group"><a href="#">Group settings</a><span class="tab-spacer"></span></li>
	<li id="group-settings-link-forum"><a href="#">Forum settings</a><span class="tab-spacer"></span></li>
	<li id="group-settings-link-room"><a href="#">Room settings</a><span class="tab-spacer"></span></li>
</ul>
</div>

	<a class="topdialog-exit" href="#" id="dialog-group-settings-exit">X</a>
	<div class="topdialog-body" id="dialog-group-settings-body">
<p style="text-align:center"><img src="{{ site.staticContentPath }}/web-gallery/images/progress_bubbles.gif" alt="" width="29" height="6" /></p>
	</div>
</div>	

<script language="JavaScript" type="text/javascript">
Event.observe("dialog-group-settings-exit", "click", function(e) {
    Event.stop(e);
    closeGroupSettings();
}, false);
</script><div class="cbb topdialog black" id="group-memberlist">
	
	<div class="box-tabs-container">
<ul class="box-tabs">
	<li class="selected" id="group-memberlist-link-members"><a href="#">Members</a><span class="tab-spacer"></span></li>
	<li id="group-memberlist-link-pending"><a href="#">Pending members</a><span class="tab-spacer"></span></li>
</ul>
</div>

	<a class="topdialog-exit" href="#" id="group-memberlist-exit">X</a>
	<div class="topdialog-body" id="group-memberlist-body">
<div id="group-memberlist-members-search" class="clearfix">
    
    <a id="group-memberlist-members-search-button" href="#" class="new-button"><b>Search</b><i></i></a>
    <input type="text" id="group-memberlist-members-search-string"/>
</div>
<div id="group-memberlist-members" style="clear: both"></div>

<div id="group-memberlist-members-buttons" class="clearfix">
	{% if (hasMember and groupMember.getMemberRank().getRankId() == 3) %}
	<a href="#" class="new-button group-memberlist-button-disabled" id="group-memberlist-button-give-rights"><b>Give rights</b><i></i></a>
	<a href="#" class="new-button group-memberlist-button-disabled" id="group-memberlist-button-revoke-rights"><b>Revoke rights</b><i></i></a>{% endif %}
	<a href="#" class="new-button group-memberlist-button-disabled" id="group-memberlist-button-remove"><b>Remove</b><i></i></a>
	<a href="#" class="new-button group-memberlist-button" id="group-memberlist-button-close"><b>Close</b><i></i></a>
</div> 
<div id="group-memberlist-pending" style="clear: both"></div>
<div id="group-memberlist-pending-buttons" class="clearfix">
	<a href="#" class="new-button group-memberlist-button-disabled" id="group-memberlist-button-accept"><b>Accept</b><i></i></a>
	<a href="#" class="new-button group-memberlist-button-disabled" id="group-memberlist-button-decline"><b>Reject</b><i></i></a>
	<a href="#" class="new-button group-memberlist-button" id="group-memberlist-button-close2"><b>Close</b><i></i></a>
</div>
	</div>
</div>

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>

{% endif %}