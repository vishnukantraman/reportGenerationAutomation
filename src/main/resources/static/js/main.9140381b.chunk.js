(this["webpackJsonpreport-generation-client"]=this["webpackJsonpreport-generation-client"]||[]).push([[0],{153:function(e,t,a){"use strict";a.r(t);var n=a(3),r=a.n(n),l=a(21),o=a.n(l),c=(a(71),a(59)),i=a(60),u=a(65),s=a(64),m=a(30),h=a(39),d=a.n(h),p=(a(72),a(61)),E=a.n(p);function b(){var e=Object(n.useState)(new Date),t=Object(m.a)(e,2),a=t[0],l=t[1],o=Object(n.useState)(new Date),c=Object(m.a)(o,2),i=c[0],u=c[1],s=Object(n.useState)(""),h=Object(m.a)(s,2),p=h[0],b=h[1],g={startDate:f(a),endDate:f(i),path:function(){var e=p.replace(/\\/g,"\\\\");p.endsWith("\\\\")||(e+="\\\\");return e}()};function f(e){var t=new Date(e),a=("0"+(t.getMonth()+1)).slice(-2);return[("0"+t.getDate()).slice(-2),a,e.getFullYear()].join("-")}return r.a.createElement("table",null,r.a.createElement("tr",null,r.a.createElement("td",null,r.a.createElement("label",{htmlFor:"startDate"}," Start Date",r.a.createElement(d.a,{showPopperArrow:!1,selected:a,dateFormat:"dd/MM/yyyy",onChange:function(e){return l(e)}}))),r.a.createElement("td",null,r.a.createElement("label",{htmlFor:"startDate"}," End Date",r.a.createElement(d.a,{showPopperArrow:!1,selected:i,dateFormat:"dd/MM/yyyy",onChange:function(e){return u(e)}})))),r.a.createElement("tr",null,r.a.createElement("label",{htmlFor:"path"},"File Path",r.a.createElement("input",{type:"text",value:p,onChange:function(e){b(e.target.value)}}))),r.a.createElement("tr",null,r.a.createElement("button",{type:"button",onClick:function(e){null==a||null==i?alert("StartDate or EndDate is missing"):a>i?alert("StartDate must be less than EndDate"):E.a.post("http://localhost:8080/getPaymentDetails",g).then((function(e){alert(e.data.replace(/\\\\/g,"\\"))}))}},"Submit")))}var g=function(e){Object(u.a)(a,e);var t=Object(s.a)(a);function a(){return Object(c.a)(this,a),t.apply(this,arguments)}return Object(i.a)(a,[{key:"render",value:function(){return r.a.createElement("div",{className:"container"},r.a.createElement(b,null))}}]),a}(n.Component);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));o.a.render(r.a.createElement(r.a.StrictMode,null,r.a.createElement(g,null)),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))},66:function(e,t,a){e.exports=a(153)},71:function(e,t,a){}},[[66,1,2]]]);
//# sourceMappingURL=main.9140381b.chunk.js.map