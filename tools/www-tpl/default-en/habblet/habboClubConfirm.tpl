<div id="hc_confirm_box">

    <img src="{{ site.staticContentPath }}/web-gallery/images/piccolo_happy.gif" alt="" align="left" style="margin:10px;" />
<p><b>Confirmation</b></p>
<p>{{ clubMonths }} {{ site.siteName }} Club month ({{ clubDays }} days) costs {{ clubCredits }} Coins. You Currently Have: {{ playerDetails.credits }} Coins.</p>

<p>
<a href="#" class="new-button" onclick="habboclub.closeSubscriptionWindow(); return false;">
<b>Cancel</b><i></i></a>
<a href="#" ondblclick="habboclub.showSubscriptionResultWindow({{ clubType }},''); return false;" onclick="habboclub.showSubscriptionResultWindow({{ clubType }},''); return false;" class="new-button">
<b>Ok</b><i></i></a>
</p>

</div>

<div class="clear"></div>