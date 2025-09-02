{% if session.loggedIn %}

<p>
{% if playerDetails.hasClubSubscription() %}
You have {{ hcDays }} {{ site.siteName }} Club day(s) left.
{% else %}
You are not a member of {{ site.siteName }} Club

{% endif %}
</p>

{% endif %}