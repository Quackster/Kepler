var HabboClient={
	
	windowName:"client",
	windowParams:"toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,",
	narrowSizeParams:"width=740,height=620",
	wideSizeParams:"width=980,height=620",
	nowOpening:false,
	
	openOrFocus:function(c)
	{
		if(HabboClient.nowOpening)
		{
			return
		}
		HabboClient.nowOpening=true;
		var e=(c.href?c.href:c);
		if(screen.width<990)
		{
			e+=((e.indexOf("?")!=-1)?"&":"?")+"wide=false"
		}
		var d=HabboClient._openEmptyHabboWindow(HabboClient.windowName);
		var b=false;
		try
		{
			b=(d.habboClient&&d.document.habboLoggedIn==true)
		}
		catch(a)
		{
		}
		
		if(b)
		{
			d.focus();
			if(d.updateHabboCount)
			{
				d.updateHabboCount($("topbar-count").innerHTML)
			}
		}
		else
		{
			d.location.href=e;
			d.focus()
		}
		HabboClient.nowOpening=false;
		if(window.location.href.indexOf("/register/welcome")!=-1)
		{
			window.location.href=habboReqPath+"/me?_notrack=1"}
			Cookie.set("habboclient","1")
		}
	},
	
	close:function(c){
		var a=Cookie.get("habboclient");
		if(a||c)
		{
			var b=HabboClient._openEmptyHabboWindow(HabboClient.windowName);
			if(!c)
			{
				Cookie.erase("habboclient")
			}
			if(b&&!b.closed)
			{
				b.close()
			}
		}
	},
	
	roomForward:function(e,d,c)
	{
		var f=(e.href?e.href:e);
		var b=false;
		try
		{
			b=window.habboClient
		}catch(a){}
		
		if(b&&!$(e).hasClassName("bbcode-client-link"))
		{
			window.location.href=f;
			return
		}
		if(document.habboLoggedIn)
		{
			new Ajax.Request("/components/roomNavigation",
			{
				method:"get",
				parameters:"targetId="+d+"&roomType="+c+"&move=true"
			},false)
		}
		
		HabboClient.openOrFocus(f)
	},
	
	closeHabboAndOpenMainWindow:function(a)
	{
		if(window.opener!=null&&!window.opener.closed)
		{
			window.opener.location.href=a.href;
			window.opener.focus()
		}
		else
		{
			var b=window.open(a.href,"_blank",HabboClient.windowParams+(screen.width>=990?HabboClient.wideSizeParams:HabboClient.narrowSizeParams));
			b.focus()
		}
		
		window.close()
	},
	
	preloadImages:function()
	{
		new Image().src=habboStaticFilePath+"/v2/images/client/preload.png";
		new Image().src=habboStaticFilePath+"/v2/images/client/grid.png";
		HabboClient.preloadImages=Prototype.emptyFunction
	},
	
	_openHabboWindow:function(a,b)
	{
		return window.open(a,b,HabboClient.windowParams+(screen.width>=990?HabboClient.wideSizeParams:HabboClient.narrowSizeParams))
	},
	
	_openEmptyHabboWindow:function(a)
	{
		return HabboClient._openHabboWindow("",a)
	},
	
	startPingListener:function()
	{
		setInterval(function()
		{
			var a=Cookie.get("xwindow_comm");
			if(a=="ping")
			{
				Cookie.set("xwindow_comm","pong")
			}
		},300)
	},
	
	isClientPresent:function(a)
	{
		Cookie.set("xwindow_comm","ping");
		setTimeout(function()
		{
			var b=Cookie.get("xwindow_comm");
			a(b=="pong")
		},800)
	}
};