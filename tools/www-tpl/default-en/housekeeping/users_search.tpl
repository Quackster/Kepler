{% include "housekeeping/base/header.tpl" %}
  <body>
    {% set searchUsersActive = " active " %}
	{% include "housekeeping/base/navigation.tpl" %}
     <h1 class="mt-4">Search Users</h1>
		{% include "housekeeping/base/alert.tpl" %}
		<p>Here you can search users by the field of your choice, and the requested input by you</p>
		<form class="table-responsive col-md-4" method="post">
			<div class="form-group">
				<label for="field">Field</label>
				<select name="searchField" class="form-control" id="field">
					<option value="username">Username</option>
					<option value="id">ID</option>
					<option value="credits">Credits</option>
					<option value="pixels">Pixels</option>
					<option value="mission">Mission</option>
				</select>
			</div>
			<div class="form-group">
				<label for="field">Search type</label>
				<select name="searchType" class="form-control" id="field">
					<option value="contains">Contains</option>
					<option value="starts_with">Starts with</option>
					<option value="ends_with">Ends with</option>
					<option value="equals">Equals</option>
				</select>
			</div>
			<div class="form-group">
				<label for="searchFor">Search data</label>
				<input type="text" name="searchQuery" class="form-control" id="searchFor" placeholder="Looking for...">
			</div>
			<button type="submit" class="btn btn-primary">Perform Search</button>
		</form>
		<br>
		{% if players|length > 0 %}
		<h2>Search Results</h2>
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
				  <th>Email</th>
				  <th>Look</th>
                  <th>Mission</th>
                  <th>Credits</th>
                  <th>Duckets</th>
				  <th>Last online</th>
				  <th>Date joined</th>
                </tr>
              </thead>
              <tbody>
			    {% set num = 1 %}
				{% for player in players %}
                <tr>
                  <td>{{ player.id }}</td>
                  <td><a href="{{ site.sitePath }}/{{ site.housekeepingPath }}/users/edit?id={{ player.id }}">{{ player.name }}</a></td>
				  <td>{{ player.email }}</td>
				  <td><img src="{{ site.sitePath }}/habbo-imaging/avatarimage?figure={{ player.figure }}&size=s"></td>
                  <td>{{ player.mission }}</td>
                  <td>{{ player.credits }}</td>
                  <td>{{ player.pixels }}</td>
				  <td>{{ player.getReadableLastOnline() }}</td>
				  <td>{{ player.getReadableJoinDate() }}</td>
                </tr>
			   {% set num = num + 1 %}
			   {% endfor %}
              </tbody>
            </table>
          </div>
		{% endif %}
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

{% include "housekeeping/base/footer.tpl" %}