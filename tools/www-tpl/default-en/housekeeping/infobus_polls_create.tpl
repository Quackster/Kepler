{% include "housekeeping/base/header.tpl" %}
  <body>
	{% set infobusPollsCreateActive = " active " %}
	{% include "housekeeping/base/navigation.tpl" %}
		<script type="text/javascript">
		var lastID = 1;
		
		function insertAfter(newNode, referenceNode) {
			referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
		}
				
		function cloneAnswer() {
			var div = document.getElementById('answer' + lastID);
			lastID++;
			clone = div.cloneNode(true); // true means clone all childNodes and all event handlers
			clone.id = "answer" + lastID;
			clone.childNodes[1].id = 'answer' + lastID + 'title';
			insertAfter(clone, div);
			document.getElementById('answer' + lastID + 'title').innerHTML = "Answer " + lastID;
		}
		
		function removeLatestAnswer() {
			if (lastID > 1) {
				var div = document.getElementById('answer' + lastID);
				div.parentNode.removeChild(div);
				lastID--;
			}
		}
		</script>
	
		<h1 class="mt-4">Create Infobus Poll</h1>
		{% include "housekeeping/base/alert.tpl" %}
		<p>Create an infobus poll for people to answer.</p>
		<form class="table-responsive col-md-4" method="post">
			<div class="form-group">
				<label>Question:</label>
				<input type="text" class="form-control" id="text" name="question"></input>
			</div>
			
			<div class="form-group" id="answer1">
				<label id="answer1title">Answer 1</label>
				<input name="answers[]" type="text" class="form-control"  placeholder="This is an option that a user can select"></input>
			</div>
			
			<p><button type="button" class="btn btn-success" onclick="cloneAnswer()">Add answer</button></p>
			<p><button type="button" class="btn btn-primary" onclick="removeLatestAnswer()">Remove answer</button></p>
			
			<div class="form-group"> 
				<input type="hidden" id="text" name="creator" value="{{ playerDetails.id }}">
				<button type="submit" class="btn btn-info">Create Poll</button>
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