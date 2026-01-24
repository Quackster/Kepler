{% if session.loggedIn %}

<div id="hc-habblet">
    <div id="hc-membership-info" class="box-content">
	{% if playerDetails.hasClubSubscription() %}
	<p>You have {{ hcDays }} {{ site.siteName }} Club day(s) left.</p>
	<p>You have been a member for {{ hcSinceMonths }} month(s)</p>
	{% else %}
	<p>You are not a member of {{ site.siteName }} Club</p>
	{% endif %}
    </div>
    <div id="hc-buy-container" class="box-content">
        <div id="hc-buy-buttons" class="hc-buy-buttons rounded rounded-hcred">
            <form class="subscribe-form" method="post">
                <input type="hidden" id="settings-figure" name="figureData" value="">
                <input type="hidden" id="settings-gender" name="newGender" value="">
                <table width="100%">
                    <tr>
                        <td>
		                    <a class="new-button fill" id="subscribe1" href="#" onclick='habboclub.buttonClick(1, "HABBO CLUB"); return false;'><b style="padding-left: 3px; padding-right: 3px;">Buy 1 month(s)</b><i></i></a>
                        </td>
                        <td width="45%">Purchase {{ clubChoiceDays1 }} days<br/> {{ clubChoiceCredits1 }} Coins</td>
                    </tr>
                    <tr>

                        <td>
		                    <a class="new-button fill" id="subscribe2" href="#" onclick='habboclub.buttonClick(2, "HABBO CLUB"); return false;'><b style="padding-left: 3px; padding-right: 3px;">Buy 3 month(s)</b><i></i></a>
                        </td>
                        <td width="45%">Purchase {{ clubChoiceDays2 }} days<br/> {{ clubChoiceCredits2 }} Coins</td>
                    </tr>
                    <tr>
                        <td>

		                    <a class="new-button fill" id="subscribe3" href="#" onclick='habboclub.buttonClick(3, "HABBO CLUB"); return false;'><b style="padding-left: 3px; padding-right: 3px;">Buy 6 month(s)</b><i></i></a>
                        </td>
                        <td width="45%">Purchase {{ clubChoiceDays3 }} days<br/> {{ clubChoiceCredits3 }} Coins</td>
                    </tr>
                </table>
            </form>
        </div>

    </div>
</div>
{% else %}
						<div id="hc-habblet" class='box-content'>
Please sign in to see your {{ site.siteName }} Club status</div>

{% endif %}