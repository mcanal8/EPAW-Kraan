<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>

<script type="text/javascript">
$(document).ready(function() {
	    $.ajaxSetup({ cache: false }); //Avoids Internet Explorer caching!
        $('#navigation').load('MenuController');
        $('#content').load('LoginController');
        $('#logout-msg').update("<small style='color:LightCoral'>Logout done!</small>");
});
</script>