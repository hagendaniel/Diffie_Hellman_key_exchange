<%@ page import="com.example.diffie_hellman_key_exchange.model.DiffieHellman" %>
<%@ page import="com.example.diffie_hellman_key_exchange.model.DiffieHellmanRepo" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: Dani
  Date: 2022. 03. 09.
  Time: 18:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%DiffieHellman d_alice = (DiffieHellman) session.getAttribute("d_alice");%>
<%DiffieHellman d_bob = (DiffieHellman) session.getAttribute("d_bob");%>
<%List<Object> eavesdrops = (List<Object>) session.getAttribute("eavesdrops");%>
<html>
<head>
    <title>Diffie-Hellman key exchange</title>
    <style type="text/css">
        html, body { margin: 0; padding: 0; }
        html, body, #customtextarea textarea { width: 99%; resize: none}
        html, body, #customtextarea1 textarea { width: 99%; resize: none}
        html, body #hackertextarea textarea{
            background: transparent;
            color: #FFF;
            resize: none;
            border: 0 none;
            width: 99%;
            outline: none;
            height: fit-content;
        }
    </style>
</head>
<body background="hetter_only.png" style="color:#FFF">
    <h1 align="center">Diffie-Hellmann key exchange</h1>
    <table border="1" style="table-layout:fixed;" WIDTH="75%"  height="80%" align="center">
        <tr>
            <th width="25%" style="overflow: auto">
                <h2>Alice</h2>
                <h3>Private key:</h3>
                <b><%=d_alice.getPrivate_key().toString()%></b>
                <b/>
                <h3>Public key:</h3>
                <%
                    String printPKey;
                    if (d_alice.getPublic_key()!=null)
                    {
                        printPKey=d_alice.getPublic_key().toString();
                    }
                    else
                    {
                        printPKey="You have to generate a public key first.";
                    }
                %>
                <b><%=printPKey%></b>
                <b></b>
                <form action="/genPKey" method="get">
                    <input type="hidden" name="user" value="alice"/>
                    <input type="submit" value="Generate public key"></input>
                </form>
                <h3>Shared key:</h3>
                <%
                    String printSKey;
                    if (d_alice.getShared_key()!=null)
                    {
                        printSKey=d_alice.getShared_key().toString();
                    }
                    else
                    {
                        printSKey="The other participant haven't sent you the public key yet.";
                    }
                %>
                <b><%=printSKey%></b>
                <b></b>
                <form action="/sendPKey" method="get">
                    <input type="hidden" name="user" value="alice"/>
                    <input type="submit" value="Request Bob's public key to generate the Shared key"></input>
                </form>
            </th>
            <th width="25%" rowspan="2" valign="top">
                <h2>Hacker</h2>
                <p>Eavesdrop:</p>
                <div id="hackertextarea">
                    <textarea rows="40">
                        <%
                            String eavesToPrint="";
                            for (Object eavesdrop : eavesdrops) {
                                eavesToPrint=eavesToPrint.concat(eavesdrop.toString()+"\n");
                            }
                        %>
                        <%=eavesToPrint%>
                    </textarea>
                </div>
            </th>
            <th width="25%"  style="overflow: auto">
                <h2>Bob</h2>
                <h3>Private key:</h3>
                <b><%=d_bob.getPrivate_key().toString()%></b>
                <b/>
                <h3>Public key:</h3>
                <%
                    String printPKey_bob;
                    if (d_bob.getPublic_key()!=null)
                    {
                        printPKey_bob=d_bob.getPublic_key().toString();
                    }
                    else
                    {
                        printPKey_bob="You have to generate a public key first.";
                    }
                %>
                <b><%=printPKey_bob%></b>
                <b></b>
                <form action="/genPKey" method="get">
                    <input type="hidden" name="user" value="bob"/>
                    <input type="submit" value="Generate public key"></input>
                </form>
                <h3>Shared key:</h3>
                <%
                    String printSKey_bob;
                    if (d_bob.getShared_key()!=null)
                    {
                        printSKey_bob=d_bob.getShared_key().toString();
                    }
                    else
                    {
                        printSKey_bob="The other participant haven't sent you the public key yet.";
                    }
                %>
                <b><%=printSKey_bob%></b>
                <b></b>
                <form action="/sendPKey" method="get">
                    <input type="hidden" name="user" value="bob"/>
                    <input type="submit" value="Request Alice's public key to generate the Shared key"></input>
                </form>
            </th>
        </tr>
        <tr>
            <td>
                <h1>Chat</h1>
                <div id="customtextarea">
                    <textarea rows="18" cols="60" style="background-color:#c1d7d7;" readonly>
                        <%
                            String toDisplay = "Messages\n"; //color was #660033 #33001a
                            for (Object message : d_alice.messages)
                            {
                                toDisplay=toDisplay.concat("Bob: "+message.toString()+"\n");
                            }
                        %>
                        <%=toDisplay%>
                    </textarea>
                </div>
                <br/>
                <form action="/sendMsg" method="get">
                    <input type="hidden" name="user" value="alice"/>
                    Message:<input type="text" name="message"/>
                    <input type="submit" value="Send"/><br/>
                </form>
                <form action="/deCryptMsg" method="get">
                    <input type="hidden" name="user" value="alice"/>
                    <input type="submit" value="Decrypt encrypted messages"/><br/>
                </form>
            </td>
            <td>
                <h1>Chat</h1>
                <div id="customtextarea1">
                    <textarea rows="18" cols="60" style="background-color:#c1d7d7" readonly>
                        <%
                            String toDisplay_bob = "Messages\n"; //color was #00264d #001a33
                            for (Object message : d_bob.messages)
                            {
                                toDisplay_bob=toDisplay_bob.concat("Alice: "+message.toString()+"\n");
                            }
                        %>
                        <%=toDisplay_bob%>
                    </textarea>
                </div>
                <br/>
                <form action="/sendMsg" method="get">
                    <input type="hidden" name="user" value="bob"/>
                    Message:<input type="text" name="message"/>
                    <input type="submit" value="Send"/><br/>
                </form>
                <form action="/deCryptMsg" method="get">
                    <input type="hidden" name="user" value="bob"/>
                    <input type="submit" value="Decrypt encrypted messages"/><br/>
                </form>
            </td>
        </tr>
    </table>
    <footer style="text-align: center">
    ©2022
        <a style="color: #c1d7d7" href="https://github.com/hagendaniel">Daniel Hagen</a>
    </footer>
</body>
</html>
