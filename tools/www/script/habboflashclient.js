var swfobject=function()
{
var aq="undefined",aD="object",ab="Shockwave Flash",X="ShockwaveFlash.ShockwaveFlash",aE="application/x-shockwave-flash",ac="SWFObjectExprInst",ax="onreadystatechange",af=window,aL=document,aB=navigator,aa=false,Z=[aN],aG=[],ag=[],al=[],aJ,ad,ap,at,ak=false,aU=false,aH,an,aI=true,ah=function()
{
var a=typeof aL.getElementById!=aq&&typeof aL.getElementsByTagName!=aq&&typeof aL.createElement!=aq,e=aB.userAgent.toLowerCase(),c=aB.platform.toLowerCase(),h=c?/win/.test(c):/win/.test(e),j=c?/mac/.test(c):/mac/.test(e),g=/webkit/.test(e)?parseFloat(e.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,d=!+"\v1",f=[0,0,0],k=null;
if(typeof aB.plugins!=aq&&typeof aB.plugins[ab]==aD)
{
k=aB.plugins[ab].description;
if(k&&!(typeof aB.mimeTypes!=aq&&aB.mimeTypes[aE]&&!aB.mimeTypes[aE].enabledPlugin))
{
aa=true;
d=false;
k=k.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
f[0]=parseInt(k.replace(/^(.*)\..*$/,"$1"),10);
f[1]=parseInt(k.replace(/^.*\.(.*)\s.*$/,"$1"),10);
f[2]=/[a-zA-Z]/.test(k)?parseInt(k.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0
}

}
else
{
if(typeof af.ActiveXObject!=aq)
{
try
{
var i=new ActiveXObject(X);
if(i)
{
k=i.GetVariable("$version");
if(k)
{
d=true;
k=k.split(" ")[1].split(",");
f=[parseInt(k[0],10),parseInt(k[1],10),parseInt(k[2],10)]
}

}

}
catch(b)
{

}

}

}
return
{
w3:a,pv:f,wk:g,ie:d,win:h,mac:j}

}
(),aK=function()
{
if(!ah.w3)
{
return
}
if((typeof aL.readyState!=aq&&aL.readyState=="complete")||(typeof aL.readyState==aq&&(aL.getElementsByTagName("body")[0]||aL.body)))
{
aP()
}
if(!ak)
{
if(typeof aL.addEventListener!=aq)
{
aL.addEventListener("DOMContentLoaded",aP,false)
}
if(ah.ie&&ah.win)
{
aL.attachEvent(ax,function()
{
if(aL.readyState=="complete")
{
aL.detachEvent(ax,arguments.callee);
aP()
}

}
);
if(af==top)
{
(function()
{
if(ak)
{
return
}
try
{
aL.documentElement.doScroll("left")
}
catch(a)
{
setTimeout(arguments.callee,0);
return
}
aP()
}
)()
}

}
if(ah.wk)
{
(function()
{
if(ak)
{
return
}
if(!/loaded|complete/.test(aL.readyState))
{
setTimeout(arguments.callee,0);
return
}
aP()
}
)()
}
aC(aP)
}

}
();
function aP()
{
if(ak)
{
return

}
try
{
var b=aL.getElementsByTagName("body")[0].appendChild(ar("span"));
b.parentNode.removeChild(b)
}
catch(a)
{
return
}
ak=true;
var d=Z.length;
for(var c=0;
c<d;
c++)
{
Z[c]()
}

}
function aj(a)
{
if(ak)
{
a()
}
else
{
Z[Z.length]=a
}

}
function aC(a)
{
if(typeof af.addEventListener!=aq)
{
af.addEventListener("load",a,false)
}
else
{
if(typeof aL.addEventListener!=aq)
{
aL.addEventListener("load",a,false)

}
else
{
if(typeof af.attachEvent!=aq)
{
aM(af,"onload",a)
}
else
{
if(typeof af.onload=="function")
{
var b=af.onload;
af.onload=function()
{
b();
a()
}

}
else
{
af.onload=a
}

}

}

}

}
function aN()
{
if(aa)
{
Y()
}
else
{
am()
}

}
function Y()
{
var d=aL.getElementsByTagName("body")[0];
var b=ar(aD);
b.setAttribute("type",aE);
var a=d.appendChild(b);
if(a)
{
var c=0;
(function()
{
if(typeof a.GetVariable!=aq)
{
var e=a.GetVariable("$version");
if(e)
{
e=e.split(" ")[1].split(",");
ah.pv=[parseInt(e[0],10),parseInt(e[1],10),parseInt(e[2],10)]
}

}
else
{
if(c<10)
{
c++;
setTimeout(arguments.callee,10);
return
}

}
d.removeChild(b);
a=null;
am()
}
)()
}
else
{
am()
}

}
function am()
{
var g=aG.length;
if(g>0)
{
for(var h=0;
h<g;
h++)
{
var c=aG[h].id;
var l=aG[h].callbackFn;
var a=
{
success:false,id:c
}
;
if(ah.pv[0]>0)
{
var i=aS(c);
if(i)
{
if(ao(aG[h].swfVersion)&&!(ah.wk&&ah.wk<312))
{
ay(c,true);
if(l)
{
a.success=true;
a.ref=av(c);
l(a)
}

}
else
{
if(aG[h].expressInstall&&au())
{
var e=
{

}
;
e.data=aG[h].expressInstall;
e.width=i.getAttribute("width")||"0";
e.height=i.getAttribute("height")||"0";
if(i.getAttribute("class"))
{
e.styleclass=i.getAttribute("class")
}
if(i.getAttribute("align"))
{
e.align=i.getAttribute("align")
}
var f=
{

}
;
var d=i.getElementsByTagName("param");
var k=d.length;
for(var j=0;
j<k;
j++)
{
if(d[j].getAttribute("name").toLowerCase()!="movie")
{
f[d[j].getAttribute("name")]=d[j].getAttribute("value")

}

}
ae(e,f,c,l)
}
else
{
aF(i);
if(l)
{
l(a)
}

}

}

}

}
else
{
ay(c,true);
if(l)
{
var b=av(c);
if(b&&typeof b.SetVariable!=aq)
{
a.success=true;
a.ref=b
}
l(a)
}

}

}

}

}
function av(b)
{
var d=null;
var c=aS(b);
if(c&&c.nodeName=="OBJECT")
{
if(typeof c.SetVariable!=aq)
{
d=c
}
else
{
var a=c.getElementsByTagName(aD)[0];
if(a)
{
d=a
}

}

}
return d
}
function au()
{
return !aU&&ao("6.0.65")&&(ah.win||ah.mac)&&!(ah.wk&&ah.wk<312)

}
function ae(f,d,h,e)
{
aU=true;
ap=e||null;
at=
{
success:false,id:h
}
;
var a=aS(h);
if(a)
{
if(a.nodeName=="OBJECT")
{
aJ=aO(a);
ad=null
}
else
{
aJ=a;
ad=h
}
f.id=ac;
if(typeof f.width==aq||(!/%$/.test(f.width)&&parseInt(f.width,10)<310))
{
f.width="310"
}
if(typeof f.height==aq||(!/%$/.test(f.height)&&parseInt(f.height,10)<137))
{
f.height="137"

}
aL.title=aL.title.slice(0,47)+" - Flash Player Installation";
var b=ah.ie&&ah.win?"ActiveX":"PlugIn",c="MMredirectURL="+af.location.toString().replace(/&/g,"%26")+"&MMplayerType="+b+"&MMdoctitle="+aL.title;
if(typeof d.flashvars!=aq)
{
d.flashvars+="&"+c
}
else
{
d.flashvars=c
}
if(ah.ie&&ah.win&&a.readyState!=4)
{
var g=ar("div");
h+="SWFObjectNew";
g.setAttribute("id",h);
a.parentNode.insertBefore(g,a);
a.style.display="none";
(function()
{
if(a.readyState==4)
{
a.parentNode.removeChild(a)
}
else
{
setTimeout(arguments.callee,10)
}

}
)()
}
aA(f,d,h)
}

}
function aF(a)
{
if(ah.ie&&ah.win&&a.readyState!=4)
{
var b=ar("div");
a.parentNode.insertBefore(b,a);
b.parentNode.replaceChild(aO(a),b);
a.style.display="none";
(function()
{
if(a.readyState==4)
{
a.parentNode.removeChild(a)
}
else
{
setTimeout(arguments.callee,10)
}

}
)()
}
else
{
a.parentNode.replaceChild(aO(a),a)
}

}
function aO(b)
{
var d=ar("div");
if(ah.win&&ah.ie)
{
d.innerHTML=b.innerHTML
}
else
{
var e=b.getElementsByTagName(aD)[0];
if(e)
{
var a=e.childNodes;
if(a)
{
var f=a.length;
for(var c=0;
c<f;
c++)
{
if(!(a[c].nodeType==1&&a[c].nodeName=="PARAM")&&!(a[c].nodeType==8))
{
d.appendChild(a[c].cloneNode(true))
}

}

}

}

}
return d
}
function aA(e,g,c)
{
var d,a=aS(c);
if(ah.wk&&ah.wk<312)
{
return d
}
if(a)
{
if(typeof e.id==aq)
{
e.id=c
}
if(ah.ie&&ah.win)
{
var f="";
for(var i in e)
{
if(e[i]!=Object.prototype[i])
{
if(i.toLowerCase()=="data")
{
g.movie=e[i]
}
else
{
if(i.toLowerCase()=="styleclass")
{
f+=' class="'+e[i]+'"'
}
else
{
if(i.toLowerCase()!="classid")
{
f+=" "+i+'="'+e[i]+'"'
}

}

}

}

}
var h="";
for(var j in g)
{
if(g[j]!=Object.prototype[j])
{
h+='<param name="'+j+'" value="'+g[j]+'" />'

}

}
a.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+f+">"+h+"</object>";
ag[ag.length]=e.id;
d=aS(e.id)
}
else
{
var b=ar(aD);
b.setAttribute("type",aE);
for(var k in e)
{
if(e[k]!=Object.prototype[k])
{
if(k.toLowerCase()=="styleclass")
{
b.setAttribute("class",e[k])
}
else
{
if(k.toLowerCase()!="classid")
{
b.setAttribute(k,e[k])

}

}

}

}
for(var l in g)
{
if(g[l]!=Object.prototype[l]&&l.toLowerCase()!="movie")
{
aQ(b,l,g[l])
}

}
a.parentNode.replaceChild(b,a);
d=b
}

}
return d
}
function aQ(b,d,c)
{
var a=ar("param");
a.setAttribute("name",d);
a.setAttribute("value",c);
b.appendChild(a)
}
function aw(a)
{
var b=aS(a);
if(b&&b.nodeName=="OBJECT")
{
if(ah.ie&&ah.win)
{
b.style.display="none";
(function()
{
if(b.readyState==4)
{
aT(a)
}
else
{
setTimeout(arguments.callee,10)
}

}
)()
}
else
{
b.parentNode.removeChild(b)
}

}

}
function aT(a)
{
var b=aS(a);
if(b)
{
for(var c in b)
{
if(typeof b[c]=="function")
{
b[c]=null
}

}
b.parentNode.removeChild(b)
}

}
function aS(a)
{
var c=null;
try
{
c=aL.getElementById(a)
}
catch(b)
{

}
return c

}
function ar(a)
{
return aL.createElement(a)
}
function aM(a,c,b)
{
a.attachEvent(c,b);
al[al.length]=[a,c,b]
}
function ao(a)
{
var b=ah.pv,c=a.split(".");
c[0]=parseInt(c[0],10);
c[1]=parseInt(c[1],10)||0;
c[2]=parseInt(c[2],10)||0;
return(b[0]>c[0]||(b[0]==c[0]&&b[1]>c[1])||(b[0]==c[0]&&b[1]==c[1]&&b[2]>=c[2]))?true:false

}
function az(b,f,a,c)
{
if(ah.ie&&ah.mac)
{
return
}
var e=aL.getElementsByTagName("head")[0];
if(!e)
{
return
}
var g=(a&&typeof a=="string")?a:"screen";
if(c)
{
aH=null;
an=null
}
if(!aH||an!=g)
{
var d=ar("style");
d.setAttribute("type","text/css");
d.setAttribute("media",g);
aH=e.appendChild(d);
if(ah.ie&&ah.win&&typeof aL.styleSheets!=aq&&aL.styleSheets.length>0)
{
aH=aL.styleSheets[aL.styleSheets.length-1]

}
an=g
}
if(ah.ie&&ah.win)
{
if(aH&&typeof aH.addRule==aD)
{
aH.addRule(b,f)
}

}
else
{
if(aH&&typeof aL.createTextNode!=aq)
{
aH.appendChild(aL.createTextNode(b+" 
{
"+f+"
}
"))
}

}

}
function ay(a,c)
{
if(!aI)
{
return
}
var b=c?"visible":"hidden";
if(ak&&aS(a))
{
aS(a).style.visibility=b
}
else
{
az("#"+a,"visibility:"+b)
}

}
function ai(b)
{
var a=/[\\\"<>\.;
]/;
var c=a.exec(b)!=null;
return c&&typeof encodeURIComponent!=aq?encodeURIComponent(b):b
}
var aR=function()
{
if(ah.ie&&ah.win)
{
window.attachEvent("onunload",function()
{
var a=al.length;
for(var b=0;
b<a;
b++)
{
al[b][0].detachEvent(al[b][1],al[b][2])
}
var d=ag.length;
for(var c=0;
c<d;
c++)
{
aw(ag[c])
}
for(var e in ah)
{
ah[e]=null

}
ah=null;
for(var f in swfobject)
{
swfobject[f]=null
}
swfobject=null
}
)
}

}
();
return
{
registerObject:function(a,e,c,b)
{
if(ah.w3&&a&&e)
{
var d=
{

}
;
d.id=a;
d.swfVersion=e;
d.expressInstall=c;
d.callbackFn=b;
aG[aG.length]=d;
ay(a,false)
}
else
{
if(b)
{
b(
{
success:false,id:a
}
)
}

}

}
,getObjectById:function(a)
{
if(ah.w3)
{
return av(a)

}

}
,embedSWF:function(k,e,h,f,c,a,b,i,g,j)
{
var d=
{
success:false,id:e
}
;
if(ah.w3&&!(ah.wk&&ah.wk<312)&&k&&e&&h&&f&&c)
{
ay(e,false);
aj(function()
{
h+="";
f+="";
var q=
{

}
;
if(g&&typeof g===aD)
{
for(var o in g)
{
q[o]=g[o]
}

}
q.data=k;
q.width=h;
q.height=f;
var n=
{

}
;
if(i&&typeof i===aD)
{
for(var p in i)
{
n[p]=i[p]
}

}
if(b&&typeof b===aD)
{
for(var l in b)
{
if(typeof n.flashvars!=aq)
{
n.flashvars+="&"+l+"="+b[l]

}
else
{
n.flashvars=l+"="+b[l]
}

}

}
if(ao(c))
{
var m=aA(q,n,e);
if(q.id==e)
{
ay(e,true)
}
d.success=true;
d.ref=m
}
else
{
if(a&&au())
{
q.data=a;
ae(q,n,e,j);
return
}
else
{
ay(e,true)
}

}
if(j)
{
j(d)
}

}
)
}
else
{
if(j)
{
j(d)
}

}

}
,switchOffAutoHideShow:function()
{
aI=false
}
,ua:ah,getFlashPlayerVersion:function()
{
return
{
major:ah.pv[0],minor:ah.pv[1],release:ah.pv[2]}

}
,hasFlashPlayerVersion:ao,createSWF:function(a,b,c)
{
if(ah.w3)
{
return aA(a,b,c)
}
else
{
return undefined
}

}
,showExpressInstall:function(b,a,d,c)
{
if(ah.w3&&au())
{
ae(b,a,d,c)
}

}
,removeSWF:function(a)
{
if(ah.w3)
{
aw(a)
}

}
,createCSS:function(b,a,c,d)
{
if(ah.w3)
{
az(b,a,c,d)
}

}
,addDomLoadEvent:aj,addLoadEvent:aC,getQueryParamValue:function(b)
{
var a=aL.location.search||aL.location.hash;
if(a)
{
if(/\?/.test(a))
{
a=a.split("?")[1]
}
if(b==null)
{
return ai(a)
}
var c=a.split("&");
for(var d=0;
d<c.length;
d++)
{
if(c[d].substring(0,c[d].indexOf("="))==b)
{
return ai(c[d].substring((c[d].indexOf("=")+1)))
}

}

}
return""
}
,expressInstallCallback:function()
{
if(aU)
{
var a=aS(ac);
if(a&&aJ)
{
a.parentNode.replaceChild(aJ,a);
if(ad)
{
ay(ad,true);
if(ah.ie&&ah.win)
{
aJ.style.display="block"
}

}
if(ap)
{
ap(at)
}

}
aU=false
}

}

}

}
();
var HabboCounter=
{
init:function(a)
{
this.refreshFrequency=a;
this.start();
this.lastValue="0"
}
,start:function()
{
new PeriodicalExecuter(this.onTimerEvent.bind(this),this.refreshFrequency)
}
,onTimerEvent:function()
{
new Ajax.Request("/components/updateHabboCount",
{
onSuccess:function(a,b)
{
if(b&&typeof b.habboCountText!="undefined"&&this.lastValue!=b.habboCountText&&$("habboCountUpdateTarget")!=null)
{
new Effect.Fade("habboCountUpdateTarget",
{
duration:0.5,afterFinish:function()
{
Element.update("habboCountUpdateTarget",b.habboCountText);
new Effect.Appear("habboCountUpdateTarget",
{
duration:0.5
}
)
}

}
);
this.lastValue=b.habboCountText
}

}

}
)
}

}
;
HabbletLoader=
{
currentPoll:null,loadedHabblets:[],loadingStatus:[],needsFlashKbWorkaround:function()
{
var a=navigator.userAgent.match(/Firefox\/(\d.\d)/);
var b=(a!=null?parseFloat(a[1]):0)>=3.5;
return HabbletLoader.isWindowsPlatform()&&((Prototype.Browser.Gecko&&!b)||Prototype.Browser.WebKit)

}
,isWindowsPlatform:function()
{
return navigator.userAgent.indexOf("Windows")>-1
}
,show:function(habbletId,habbletWrapper,data)
{
if(HabbletLoader.needsFlashKbWorkaround())
{
$("client-ui").addClassName("x-workaround");
if(habbletId=="credits"||habbletId=="fbAppRequest")
{
$("client-ui").addClassName("x-workaround-wide")

}

}
if(typeof habbletWrapper!="undefined")
{
habbletWrapper.show();
HabbletLoader.bringToTop(habbletWrapper);
if(typeof data=="string")
{
try
{
var sender=eval("__"+habbletId+"__sendmsg__");
sender.apply(null,[data])
}
catch(e)
{

}

}
if(HabbletLoader.isWindowsPlatform()&&Prototype.Browser.WebKit)
{
$("content").setStyle(
{
width:habbletWrapper.getWidth()+"px"
}
)

}

}

}
,hide:function(a)
{
if(HabbletLoader.needsFlashKbWorkaround())
{
$("client-ui").removeClassName("x-workaround");
$("client-ui").removeClassName("x-workaround-wide")
}
if(typeof a!="undefined")
{
a.hide()
}

}
,load:function(g,c)
{
var i=true;
var f=false;
var b=true;
var a=false;
var e=
{
fromHabblet:"true"
}
;
if(g=="roomenterad")
{
i=false;
b=false;
e=
{
contentWidth:$("flash-wrapper").offsetWidth
}
;
if(HabbletLoader.needsFlashKbWorkaround())
{
return
}

}
if(g=="externalLink")
{
i=false;
b=false;
a=true;
e=
{
url:c
}

}
if(g=="fbLike")
{
i=false;
b=false;
a=true;
e=
{
roomId:c
}
;
f=true
}
if(g=="fbAppRequest")
{
i=false;
a=true;
b=false;
e=c
}
if(g=="avatars")
{
a=true
}
if(typeof HabbletLoader.loadedHabblets[g]!="undefined"&&!a)
{
HabbletLoader.show(g,HabbletLoader.loadedHabblets[g],c);
return
}
if(typeof HabbletLoader.loadingStatus[g]!="undefined")
{
return
}
HabbletLoader.loadingStatus[g]=1;
var d=$("content");
if(b)
{
var h=Builder.node("div",
{
id:"loading-"+g,className:"client-habblet-container loading-element"
}
,[Builder.node("img",
{
src:habboStaticFilePath+"/v2/images/lightwindow/ajax-loading.gif"
}
),Builder.node("p",g)]);
d.appendChild(h);
HabbletLoader.bringToTop(h)
}
new Ajax.Request("/habblet/cproxy?habbletKey="+g,
{
method:"post",parameters:e,onComplete:function(n,r)
{
if(n.responseText.length==0||(r!=null&&r.disabled))
{
delete HabbletLoader.loadingStatus[g];
if(b)
{
d.removeChild($("loading-"+g))
}
return
}
var p=n.responseText.indexOf("<!-- dependencies");
if(p>-1)
{
var k=n.responseText.substring(p+17);
k=k.substring(0,k.lastIndexOf("-->"));
var q=k.match(new RegExp('<s*link rel="stylesheet".*?>',"g"));
if(q)
{
for(var o=0;
o<q.length;
o++)
{
var j=/href="(.*?)"/.exec(q[o]);
if(j.length==2)
{
HabbletLoader.loadDependency(j[1],"css")
}

}

}
var l=function()
{
var t=$(g)||Builder.node("div",
{
id:g,"class":"client-habblet-container contains-"+g+(i?" draggable":"")
}
);
d.appendChild(t);
t=$(t);
if(i&&Prototype.Browser.IE)
{
var v=parseInt(t.getStyle("right"),10);
var x=0-v-t.getWidth();
t.setStyle(
{
left:x+"px"
}
)
}
if(f&&!$("client-ui").hasClassName("x-workaround"))
{
var s=0;
var u=0;
if(document.all)
{
s=document.body.clientWidth;
u=document.body.clientHeight
}
else
{
if("innerWidth" in window)
{
s=window.innerWidth;
u=window.innerHeight
}

}
if(s!=0&&u!=0)
{
t.setStyle(
{
right:(s-t.getWidth())/2+"px"
}
);
t.setStyle(
{
top:(u-t.getHeight())/2+"px"
}
)
}

}
t.update(n.responseText.replace('document.observe("dom:loaded",',"HabbletLoader.exec(")).show();
Rounder.init();
t.select(".habblet-close").each(function(w)
{
$(w).observe("click",function()
{
HabbletLoader.hide(t)

}
)
}
);
HabbletLoader.loadedHabblets[g]=t;
setTimeout(function()
{
HabbletLoader.show(g,t,c);
if($("client-ui")&&!$("client-ui").hasClassName("x-workaround")&&i)
{
new Draggable(t,
{
handle:t.select(".title")[0],starteffect:null,endeffect:null
}
)
}

}
,300);
delete HabbletLoader.loadingStatus[g];
t.observe("click",function(w)
{
HabbletLoader.bringToTop(t)

}
);
if(b)
{
d.removeChild($("loading-"+g))
}

}
;
var m=k.match(new RegExp("<s*script.*?>","g"));
if(m)
{
for(var o=0;
o<m.length;
o++)
{
var j=/src="(.*?)"/.exec(m[o]);
if(j.length==2)
{
HabbletLoader.loadDependency(j[1],"js")
}

}
HabbletLoader.currentPoll=setInterval(function()
{
HabbletLoader.poll("__"+g+"__defined__",l)

}
,500)
}
else
{
l.apply(null)
}

}

}

}
)
}
,poll:function(statement,onReady)
{
var ready=false;
try
{
ready=eval(statement)
}
catch(e)
{

}
if(ready)
{
clearInterval(HabbletLoader.currentPoll);
onReady.apply(null)
}

}
,loadDependency:function(a,b)
{
if(b=="js")
{
var c=document.createElement("script");
c.setAttribute("type","text/javascript");
c.setAttribute("src",a)
}
else
{
if(b=="css")
{
var c=document.createElement("link");
c.setAttribute("rel","stylesheet");
c.setAttribute("type","text/css");
c.setAttribute("href",a)
}

}
if(typeof c!="undefined")
{
document.getElementsByTagName("head")[0].appendChild(c)
}

}
,exec:function(a)
{
a.apply(null)
}
,openLink:function(a)
{
while(a.tagName.toLowerCase()!="a")
{
a=a.parentNode

}
if(a.href)
{
if(window.opener!=null&&window.opener.name=="habboMain")
{
window.opener.location.href=a.href;
window.opener.focus()
}
else
{
window.open(a.href,"habboMain")
}

}

}
,bringToTop:function(b)
{
var a=$$(".client-habblet-container");
if(a.length>1)
{
var c=0;
a.each(function(d)
{
c=Math.max(d.style.zIndex,c)
}
);
b.style.zIndex=c+1

}

}
,removeHabblet:function(a)
{
var b=$("content");
if(typeof HabbletLoader.loadingStatus[a]!="undefined")
{
return
}
if(typeof HabbletLoader.loadedHabblets[a]!="undefined")
{
b.removeChild($(a));
delete HabbletLoader.loadedHabblets[a]
}

}

}
;
var FlashHabboClient=(function()
{
var a=function()
{
Event.observe(window,"unload",function()
{
Cookie.erase("habboclient");
if(FlashExternalInterface.loginLogEnabled&&!FlashExternalInterface.clientInited)
{
new Ajax.Request(habboReqPath+"/clientlog/update",
{
method:"post",parameters:
{
flashStep:"client.window.closed"
}
,asynchronous:false
}
)
}

}
)
}
;
window.habboClient=true;
ensureOpenerIsLoggedIn();
a();
var b=function()
{
swfobject.createCSS("html","height:100%;
");
swfobject.createCSS("body","height:100%;
");
swfobject.createCSS("#flash-container","margin:0;
 width:100%;
 height:100%;
")
}
;
if(typeof facebookUser=="undefined")
{
swfobject.addDomLoadEvent(b)
}
document.observe("dom:loaded",function()
{
if(!swfobject.hasFlashPlayerVersion("10.0.0"))
{
HabboView.run();
FlashExternalInterface.logLoginStep("web.flash_missing")

}

}
);
return
{
cacheCheck:function()
{
new Ajax.Request(habboReqPath+"/cacheCheck",
{
parameters:
{
flashClient:"true"
}
,onComplete:function(c)
{
if(c.responseText=="false")
{
window.location.href=window.location.href+(window.location.href.indexOf("?")>0?"&":"?")+"t"+new Date().getTime()
}

}

}
)
}

}

}
)();
var FlashExternalInterface=(function()
{
var f=null;
var b=0;
var g=null;
var d=function()
{
if(!f)
{
f=window.setInterval(function()
{
var k=new Date().getTime();
if(b<k-15*60*1000)
{
e("keepalive","");
b=k
}

}
,10*60*1000)
}

}
;
var e=function(m,k,l)
{
d();
if(l<0)
{
l=undefined
}
if(typeof flashPageTracker!="undefined")
{
flashPageTracker._trackEvent("client",m,k,l)
}
if(typeof pageTracker!="undefined")
{
pageTracker._trackEvent("client",m,k,l)

}

}
;
var j=function(k)
{
d();
if(typeof pageTracker!="undefined")
{
pageTracker._trackPageview("/client/"+k)
}
if(typeof flashPageTracker!="undefined")
{
flashPageTracker._trackPageview("/client/"+k)
}

}
;
var i=function(n)
{
if(FlashExternalInterface.nielsenUrl)
{
c();
var o=FlashExternalInterface.nielsenUrl+"/client/"+n;
var k=o;
if(g)
{
k=k+"&rp="+g
}
var m=o.match(/&si=([^&]*)/);
if(m)
{
g=m[1]
}
else
{
g=null
}
var l=new Image(1,1);
l.src=k
}

}
;
var h=null;
var c=function()
{
if(!h)
{
h=window.setInterval(function()
{
i("keepalive")
}
,15*60*1000)
}

}
;
var a=
{
authentication:function(k,l)
{
i("loggedin");
j("loggedin")
}
,navigator:function(k,l)
{
if(k=="private"||k=="public")
{
j(k+"/"+l)

}
else
{
e("navigator",k)
}

}
,catalogue:function(k,l)
{
if(k=="open")
{
e("catalogue","open")
}
else
{
e("catalogue",l.toString())
}

}
,achievement:function(k,l)
{
e("achievement",l.toString())
}
,habblet:function(k,l)
{
if(k=="news")
{
j(k+"/"+l)
}
else
{
e(k,l.toString())
}

}
,room_ad:function(k,l)
{
e("room_ad",l+"_"+k)
}

}
;
return
{
legacyTrack:function(n,k,m)
{
if("console" in window&&"log" in console)
{
console.log("action = ["+n+"], label = ["+k+"], data = ["+m+"]")

}
if("authentication"==n&&"authok"==k)
{
$(document).fire("habbo:authok")
}
var l=a[n];
if(l)
{
l.apply(this,[k,m])
}
else
{
e(n,k)
}

}
,track:function(m,k,l)
{
if("console" in window&&"log" in console)
{
console.log("action = ["+m+"], label = ["+k+"], value = ["+l+"]")
}
e(m,k,l)
}
,logError:function(k)
{
if("console" in window)
{
console.log("errorCode = "+k)

}

}
,logWarn:function(k)
{
new Ajax.Request(habboReqPath+"/habbo/flash_client_warning",
{
method:"post",parameters:k
}
)
}
,logLoginStep:function(k,l)
{
setTimeout(function()
{
if(k=="client.init.auth.ok")
{
FlashExternalInterface.clientInited=true
}
if(FlashExternalInterface.loginLogEnabled)
{
if(Object.isUndefined(l)||l==null)
{
new Ajax.Request(habboReqPath+"/clientlog/update",
{
method:"post",parameters:
{
flashStep:k
}

}
)

}
else
{
new Ajax.Request(habboReqPath+"/clientlog/update",
{
method:"post",parameters:
{
flashStep:k,data:l
}

}
)
}

}

}
,100)
}
,openHabblet:function(k,l)
{
HabbletLoader.load(k,l);
FlashExternalInterface.legacyTrack("habblet",k,"open")
}
,postAchievement:function(k,l)
{
if(FacebookIntegration)
{
FacebookIntegration.publishAchievementStory(k,l)

}

}
,postAchievementShareBonus:function(k,n,m,l)
{
if(FacebookIntegration)
{
FacebookIntegration.publishAchievementScoreBonus(k,n,m,l)
}

}
,postXmasViral:function(k,o,n,l,m)
{
if(FacebookIntegration)
{
FacebookIntegration.publishXmasViral(k,o,n,l,m||"feed")
}

}
,openExternalLink:function(k)
{
HabbletLoader.load("externalLink",k)

}
,fbLike:function(k)
{
HabbletLoader.load("fbLike",k)
}
,logout:function()
{
if(window.opener)
{
window.opener.location=FlashExternalInterface.signoutUrl;
window.close()
}
else
{
window.location=FlashExternalInterface.signoutUrl
}

}
,embedSwfCallback:function(k)
{
if(k&&k.success)
{
FlashExternalInterface.clientElement=k.ref

}

}
,authenticateFacebook:function()
{
FacebookIntegration.getAccessToken("$
{
restFbApiHelper.facebookCookieName
}
",'$
{
restFbApiHelper.extendedPermissions!""
}
')
}

}

}
)();
FlashExternalInterface.loginLogEnabled=false;
FlashExternalInterface.nielsenUrl=null;
FlashExternalInterface.clientInited=false;
FlashExternalInterface.clientElement=null;
var ExternalClickHandler=
{
trackClick:function(a,d,b)
{
if(b)
{
var c=window.open(a+"&hash="+d,"_blank","menubar=1,status=1,resizable=1,scrollbars=1,location=1,toolbar=1");
if(window.focus)
{
c.focus()
}

}
HabbletLoader.hide($("externalLink"));
new Ajax.Request("/habblet/external_link",
{
parameters:
{
url:a,hash:d,clicked:b
}

}
)

}
,clickCancel:function(a,b)
{
ExternalClickHandler.trackClick(a,b,false)
}
,clickContinue:function(a,b)
{
ExternalClickHandler.trackClick(a,b,true)
}

}
;
var RightClick=
{
init:function(b,a)
{
this.FlashObjectID=a;
this.FlashContainerID=b;
this.Cache=this.FlashObjectID;
if(window.addEventListener)
{
window.addEventListener("mousedown",this.onGeckoMouse(),true)

}
else
{
document.getElementById(this.FlashContainerID).onmouseup=function()
{
document.getElementById(RightClick.FlashContainerID).releaseCapture()
}
;
document.oncontextmenu=function()
{
if(window.event.srcElement.id==RightClick.FlashObjectID)
{
return false
}
else
{
RightClick.Cache="nan"
}

}
;
document.getElementById(this.FlashContainerID).onmousedown=RightClick.onIEMouse

}

}
,killEvents:function(a)
{
if(a)
{
if(a.stopPropagation)
{
a.stopPropagation()
}
if(a.preventDefault)
{
a.preventDefault()
}
if(a.preventCapture)
{
a.preventCapture()
}
if(a.preventBubble)
{
a.preventBubble()
}

}

}
,onGeckoMouse:function(a)
{
return function(b)
{
if(b.button!=0)
{
RightClick.killEvents(b);
if(b.target.id==RightClick.FlashObjectID&&RightClick.Cache==RightClick.FlashObjectID)
{
RightClick.call()

}
RightClick.Cache=b.target.id
}

}

}
,onIEMouse:function()
{
if(event.button>1)
{
if(window.event.srcElement.id==RightClick.FlashObjectID&&RightClick.Cache==RightClick.FlashObjectID)
{
RightClick.call()
}
document.getElementById(RightClick.FlashContainerID).setCapture();
if(window.event.srcElement.id)
{
RightClick.Cache=window.event.srcElement.id

}

}

}
,call:function()
{

}

}
;
var Embed=
{
embedWindowName:"embed",docWindowName:"habboMain",docWindowParams:"toolbar=yes,location=yes,directories=yes,status=yes,scrollbars=yes,resizable=yes",rpxWindowName:"rpxLogin",rpxWindowParams:"toolbar=no,location=yes,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=750,height=440",setEmbedWindowName:function()
{
window.name=Embed.embedWindowName

}
,openRpxLoginPopup:function(b,a,d)
{
var c=window.open(b.href,Embed.rpxWindowName,Embed.rpxWindowParams);
if(window.focus)
{
c.focus()
}

}
,openSigninPopup:function(a)
{
var d=(screen.width/2)-(700/2);
var c=(screen.height/2.5)-(450/2);
var b=window.open(a.href,null,"toolbar=no,location=yes,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=700,height=450,left="+d+",top="+c);
if(window.focus)
{
b.focus()
}

}
,openFullscreenHabbo:function(b,a)
{
window.name="old-client";
HabboClient.openOrFocus(b);
window.location.href=a||"/embed"
}
,decorateNaviLinks:function()
{
$$("#navi a").each(function(a)
{
Event.observe(a,"click",function(d)
{
Event.stop(d);
var b=Event.element(d);
var c=window.open(b.href,"_blank",Embed.docWindowParams);
if(window.focus)
{
c.focus()
}

}
)
}
)
}
,registerUnloadReloadHook:function()
{
Event.observe(window,"unload",function()
{
if(window.opener&&window.opener!=window)
{
window.opener.location.replace(window.opener.location.href)
}

}
)
}
,decorateFooterLinks:function()
{
$$("#footer p a").each(function(a)
{
Event.observe(a,"click",function(d)
{
Event.stop(d);
var b=Event.element(d);
var c=window.open(b.href,"_blank",Embed.docWindowParams);
if(window.focus)
{
c.focus()
}

}
)
}
)
}
,decorateLogoLink:function()
{
$$("#right-buttons h1 a").each(function(a)
{
Event.observe(a,"click",function(d)
{
Event.stop(d);
var b=Event.element(d);
var c=window.open(b.href,Embed.docWindowName,Embed.docWindowParams);
if(window.focus)
{
c.focus()
}

}
)
}
)
}

}
;
