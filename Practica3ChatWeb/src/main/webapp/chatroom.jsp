<!DOCTYPE html>
<html>
	<head>
		<script> 
		setInterval(refreshIframe, 5000); //establece el tiempo a 5 seg.
		function refreshIframe() {  //recarga el iframe de la página
			frames[0].location.reload(true);
		}
		function sendMessages()
		{
			var value=document.forms["sMsg"]["msg"].value; 
			var encrypt=document.forms["sMsg"]["cypher"].checked;
			if (value==null || value==""){
				alert("The message can't be empty"); return false;
			}
			document.forms["sMsg"]["isEncrypted"].value="false";
			if (encrypt){
				//Encriptamos
				value=cesar(value,key,false);
				document.forms["sMsg"]["isEncrypted"].value="true";
			}
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
		<meta charset="ISO-8859-1">
		<title>Chat Room</title>
	</head>
	<body>
		<h1>Chat Room</h1>
		<h3>You are logged as <%= session.getAttribute("nickname") %></h3>
		<br/>
		<iframe src="conversation" width=360 height=150>Error loading chat messages</iframe>
		<form id="form2" action="sendMsg" method="post" onsubmit="return sendMessages()" name="sMsg">
			<textarea id="fieldMsg" name="msg" rows="4" cols="40" defaultValue="Write your message here"></textarea>
			<input id="boxCypher" type="checkbox" name="cypher" > Encrypted
			<br/>	
			<input id="buttonSend" type="submit" name="send" value="Send" />
			<input id="buttonClear" type="reset" name="clear" value="Clear" />
			<input id="hiddenMsg" type="hidden" name="message"/>
			<input id="hiddenCyph" type="hidden" name="isEncrypted"/>

		</form>   
		<br/>
		<a href="logoutUser?nick=${users} ">Logout</a>
		<br/>	 
	</body>
</html>