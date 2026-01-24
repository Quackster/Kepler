<div class="avatar-list-info-container">
	<div class="avatar-info-basic clearfix">
		<div class="avatar-list-info-close-container"><a href="#" class="avatar-list-info-close" id="avatar-list-info-close-3"></a></div>
		<div class="avatar-info-image">
						<img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ avatar.getFigure() }}&size=b&direction=4&head_direction=4&crr=0&gesture=&frame=1" alt="test" />
		</div>
<h4><a href="{{ site.sitePath }}/home/{{ avatar.getName() }}">{{ avatar.getName() }}</a></h4>
<p>
<a href="{{ site.sitePath }}/client" target="client" onclick="HabboClient.openOrFocus(this); return false;">

{% if avatar.isOnline() and avatar.isProfileVisible() %}
			<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/profile/habbo_online_anim.gif" />
{% else %}
			<img src="{{ site.staticContentPath }}/web-gallery/images/myhabbo/profile/habbo_offline.gif" />
{% endif %}

</p>
<p>Habbo created on: <b>{{ avatar.getCreatedAt() }}</b></p>
<p><a href="{{ site.sitePath }}/home/{{ avatar.getName() }}" class="arrow">View Habbo page</a></p>
	</div>
</div>