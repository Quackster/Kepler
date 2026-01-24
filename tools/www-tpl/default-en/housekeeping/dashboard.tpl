{% include "housekeeping/base/header.tpl" %}
  <body>
	{% set dashboardActive = " active " %}
	{% include "housekeeping/base/navigation.tpl" %}
     <h1 class="mt-4">Hotel Statistics</h1>
		  <p>Welcome to the housekeeping for {{ site.siteName }} Hotel, here you can manage a lot of things at once, such as users, news, site content and view the statistics of the hotel.</p>
		   <div class="table-responsive col-md-4">
            <table class="table table-striped">
			<thead>
				<tr>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<tbody class="col-md-4">
				<tr>
					<td><strong>Havana Web Version</strong></td>
					<td>1.0</td>
				</tr>
				<tr>
					<td>Users</td>
					<td>{{ stats.userCount }}</td>
				</tr>
				<tr>
					<td>Room Items</td>
					<td>{{ stats.roomItemCount }}</td>
				</tr>
				<tr>
					<td>Inventory Items</td>
					<td>{{ stats.inventoryItemsCount }}</td>
				</tr>
				<tr>
					<td>Groups</td>
					<td>{{ stats.groupCount }}</td>
				</tr>
				<tr>
					<td>Pets</td>
					<td>{{ stats.petCount }}</td>
				</tr>
				<tr>
					<td>Photos</td>
					<td>{{ stats.photoCount }}</td>
				</tr>
			</tbody>
			</table>
		  </div>
          <h2>Newest Players</h2>
		  <p>The recently joined player list is seen below</p>
		  <div style="margin:10px">
			{% set zeroCoinsValue = '' %}
			{% if zeroCoinsFlag %}
				{% set zeroCoinsValue = '&zerocoins' %}
			{% endif %}
			
			{% if nextPlayers|length > 0 %}
				{% set ourNextPage = page + 1 %}
				<a href="{{ site.sitePath }}/{{ site.housekeepingPath }}?page={{ ourNextPage }}{{ zeroCoinsValue }}&sort={{ sortBy }}"><button type="button" class="btn btn-info">Next Page</button></a>
			{% endif %}
			{% if previousPlayers|length > 0 %}
				{% set ourNextPage = page - 1 %}
				<a href="{{ site.sitePath }}/{{ site.housekeepingPath }}?page={{ ourNextPage }}{{ zeroCoinsValue }}&sort={{ sortBy }}"><button type="button" class="btn btn-warning">Go back</button></a>
			{% endif %}
			
			{% if zeroCoinsFlag %}
				<a href="{{ site.sitePath }}/{{ site.housekeepingPath }}"><button type="button" class="btn btn-warning">View players with coins</button></a>
			{% else %}
				<a href="{{ site.sitePath }}/{{ site.housekeepingPath }}?zerocoins"><button type="button" class="btn btn-warning">View players without coins</button></a>
			{% endif %}
			</div>
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
				  <th>Email</th>
				  <th>Look</th>
        <th>Motto</th>
        <th>Credits</th>
        <th>Pixels</th>
        <th><a href="{{ site.sitePath }}/{{ site.housekeepingPath }}?page={{ page }}{{ zeroCoinsValue }}&sort=last_online">Last online</a></th>
        <th><a href="{{ site.sitePath }}/{{ site.housekeepingPath }}?page={{ page }}{{ zeroCoinsValue }}&sort=created_at">Date joined</a></th>
				  <th></th>
                </tr>
              </thead>
              <tbody>
			    {% set num = 1 %}
				{% for player in players %}
                <tr>
                  <td>{{ player.id }}</td>
                  <td><a href="{{ site.sitePath }}/{{ site.housekeepingPath }}/users/edit?id={{ player.id }}">{{ player.name }}</a> - <a href="{{ site.sitePath }}/{{ site.housekeepingPath }}/transaction/lookup?searchQuery={{ player.getName() }}">Transactions</a></td>
				  <td>{{ player.email }}</td>
				  <td><img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ player.figure }}&size=s"></td>
									{% autoescape 'html' %}
                  <td>{{ player.motto }}</td>
				 					{% endautoescape %}
                  <td>{{ player.credits }}</td>
                  <td>{{ player.pixels }}</td>
				  <td>{{ player.formatLastOnline("dd-MM-yyyy HH:mm:ss") }}</td>
				  <td>{{ player.formatJoinDate("dd-MM-yyyy HH:mm:ss") }}</td>
				  <td><a href="{{ site.sitePath }}/{{ site.housekeepingPath }}/api/ban?username={{ player.name }}"><button type="button" class="btn btn-success">Permanently Ban User</button></a></td>
                </tr>
			   {% set num = num + 1 %}
			   {% endfor %}
              </tbody>
            </table>
      </div>
    </div>
  </div>
  <script src="https://code.jquery.com/jquery-3.1.1.slim.min.js"></script>
  <script src="https://blackrockdigital.github.io/startbootstrap-simple-sidebar/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script>
    $("#menu-toggle").click(function(e) {
      e.preventDefault();
      $("#wrapper").toggleClass("toggled");
    });
  </script>
</body>
</html>