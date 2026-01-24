<input type="hidden" name="webwork.token.name" value="webwork.token"/>
<input type="hidden" name="webwork.token" value="HavanaWeb"/>
<div id="group-logo">
   <img src="{{ site.sitePath }}/web-gallery/images/groups/group_icon.gif" alt="" width="46" height="46" />
</div>

<p>
Group name: <b>{{ groupName }}</b>.<br>Price: <b>{{ groupCost }} Credits</b>.<br> You have: <b>{{ playerDetails.credits }} Credits</b>.
</p>

<div id="group-confirmation-button-area">	
<div class="new-buttons clearfix">
	<a class="new-button" href="#" onclick="GroupPurchase.close(); return false;"><b>Cancel</b><i></i></a>	
	<a class="new-button" href="#" onclick="GroupPurchase.purchase(); return false;"><b>Buy this</b><i></i></a>
</div>
</div>