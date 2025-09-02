{% if playerDetails.hasClubSubscription() == false %}
	<div class="cbb habboclub-tryout">
        <h2 class="title">Join {{ site.siteName }} Club</h2>
        <div class="box-content">
            <div class="habboclub-banner-container habboclub-clothes-banner"></div>
            <p class="habboclub-header">{{ site.siteName }} Club is our VIP members-only club: absolutely no riff-raff admitted! Members enjoy a wide range of benefits, including exclusive clothes, free gifts and an extended Friend List.</p>

            <p class="habboclub-link"><a href="{{ site.sitePath }}/club">Check out {{ site.siteName }} Club &gt;&gt;</a></p>
        </div>
    </div>
{% endif %}