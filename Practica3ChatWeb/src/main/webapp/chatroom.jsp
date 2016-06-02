<!DOCTYPE html>
<html>
	<head>
		<title>Chat Room</title>
		<meta charset="ISO-8859-1">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="author" content="Borja Gete">
        <meta name="author" content="Plamen Peytov">
        <meta name="description" content="Pr�ctica 3 ChatWeb">
		<script> 
		setInterval(refreshIframe, 5000); //establece el tiempo a 5 seg.
		function refreshIframe() {� //recarga el iframe de la página
			frames[0].location.reload(true);
			frames[0].scrollBy(0,frames[0].innerHeight*5)
		}
		function sendMessages()
		{
			var value=document.forms["sMsg"]["msg"].value; 
			var encrypt=document.forms["sMsg"]["cypher"].checked;
			var key=document.forms["sMsg"]["theKey"].value;
			if (value==null || value==""){
				alert("The message can't be empty"); return false;
			}
			if (encrypt){
				//Encriptamos
				value=cesar(value,key,true);
				//value=value2
			}
			document.forms["sMsg"]["isEncrypted"].value=encrypt;
			//Rellenamos el campo oculto
			document.forms["sMsg"]["message"].value=value;
		}
		//Función de encriptado
		function cesar(txt,n,space){  
		    //Comprobamos que el texto es válido  
		    var pat=/^[0-9A-z\s]*$/  
		    if(!pat.test(txt)) return 'Texto no válido'  
		    //Adaptamos el texto según si admitimos espacios  
		    var let='abcdefghijklmnopqrstuvwxyz'+(space?' ':'')  
		    var txt=txt.toLowerCase()  
		    if(!space) for(var a=0;a<txt.length;a++) txt=txt.replace(' ','')  
		    //Ajustamos el desplazamiento de "n"  
		    var n=n%let.length,r=''  
		        n=n<0?let.length+n:n  
		    for(var a=0;a<txt.length;a++){  
		        //Desplazamos el abecedario  
		        l=let.charAt((let.indexOf(txt.charAt(a))+n)%let.length)  
		        r+=l.length==0?' ':l  
		    }  
		    return r  
		} 
		</script>
		<style>
			body{
			    background-color: hsla(120, 100%, 25%, 0.3);;
			    font-family: Calibri,Verdana,Serif,Arial;
			}
			textarea{
				background-color: hsla(25%, 100%, 75%, 0.3);;
			    font-family: Calibri,Verdana,Serif,Arial;
			}
			div{
				background-color: linen;
				align: center;
				border-radius: 10px;
			}
			p{align:center;}
			div#contenedor{
				padding: 1em 3em;
 				margin: 3em 12% auto;
			}
			b{
				text-decoration: underline;
				text-color:blue
			}
			i{
				text-align: right;
			}
		</style>
	</head>
	<body>
	<div id="contenedor">
		<h1>Chat Room</h1>
		<h3>You are logged as <b><%= session.getAttribute("nickname") %></b></h3>
		<div id="formulario">
		<br/>
		<iframe src="conversation" width=360 height=150>Error loading chat messages</iframe>
		<form id="form2" action="sendMsg" method="post" onsubmit="return sendMessages()" name="sMsg">

			<input id="buttonRefresh" type="button" name="refresh" value="Refresh" onclick="return refreshIframe()" />
			<br/>
			<textarea id="fieldMsg" name="msg" rows="4" cols="40" defaultValue="Write your message here"></textarea>
			<input id="boxCypher" type="checkbox" name="cypher" ><i>Encrypted</i> 
			<br/>	
			<input id="buttonSend" type="submit" name="send" value="Send" />
			<input id="buttonClear" type="reset" name="clear" value="Clear" />
			<input id="hiddenMsg" type="hidden" name="message"/>
			<input id="hiddenCyph" type="hidden" name="isEncrypted"/>
			<input id="hiddenKey" type="hidden" name="theKey" value="<%=session.getAttribute("key")%>"/>
		</form>   
		<br/>
		<a href="logoutUser?nick=${users} ">Logout</a>
		<br/>
		</div>
		</div>
	</body>
</html>