{% include "housekeeping/base/header.tpl" %}
  <body>
    {% set roomCreateAdsActive = " active" %}
	{% include "housekeeping/base/navigation.tpl" %}
     <h1 class="mt-4">Create Ad</h1>
		{% include "housekeeping/base/alert.tpl" %}
		<p>Create a room ad that will display as a billboards from within the hotel.</p>
		<form class="table-responsive col-md-4" method="post">
			<div class="form-group">
				<label>Room ID</label>
				<input type="text" class="form-control" name="roomid">
			</div>
			<div class="form-group">
				<label>URL</label>
				<input type="text" class="form-control" name="url">
			</div>
			<div class="form-group">
				<label>Image</label>
				<input type="text" class="form-control" name="image">
			</div>
			<div class="form-group">
				<label>Enabled</label>
				<input type="checkbox" name="enabled" checked />
			</div>
			<div class="form-group">
				<label>Room loading/intermission ad</label>
				<input type="checkbox" name="loading-ad"/>
			</div>
			<div class="form-group"> 
				<button type="submit" class="btn btn-info">Create Ad</button>
			</div>
		</form>
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