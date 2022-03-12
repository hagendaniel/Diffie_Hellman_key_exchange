package com.example.diffie_hellman_key_exchange.controller;

import com.example.diffie_hellman_key_exchange.logic.AESEncryption;
import com.example.diffie_hellman_key_exchange.model.DiffieHellman;
import com.example.diffie_hellman_key_exchange.model.DiffieHellmanRepo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

@Controller
public class WebController {
    @GetMapping("/")
    public String loginPage(HttpSession session)
    {
        DiffieHellman d_alice = new DiffieHellman(5);
        session.setAttribute("d_alice",d_alice);
        DiffieHellman d_bob = /*DiffieHellmanRepo.getInstance(5).getDiffieHellman();*/new DiffieHellman(5);
        session.setAttribute("d_bob",d_bob);
        String eavesdrop = "Eavesdrop:\n";
        session.setAttribute("eavesdrop", eavesdrop);
        return "index.jsp";
    }

    @GetMapping("/genPKey")
    public String genPKey(@RequestParam("user") String user, HttpSession session)
    {
        if(user.equals("alice"))
        {
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            d_alice.genPublic_key();
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            d_bob.genPublic_key();
        }

        return "index.jsp";
    }

    @GetMapping("/sendPKey")
    public String genSKey(@RequestParam("user") String user, HttpSession session)
    {
        if(user.equals("alice"))
        {
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            String eavesdrop = (String)session.getAttribute("eavesdrop");
            d_alice.genSharedKey(d_bob.getPublic_key());
            eavesdrop+=(d_bob.getPublic_key()+"\n");
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            String eavesdrop = (String)session.getAttribute("eavesdrop");
            d_bob.genSharedKey(d_alice.getPublic_key());
            eavesdrop+=(d_alice.getPublic_key()+"\n");
        }
        return "index.jsp";
    }

    @GetMapping("/sendMsg")
    public String sendMsg(@RequestParam("user") String user, @RequestParam("message") String message, HttpSession session)
    {
        AESEncryption aesEncryption = new AESEncryption();
        if(user.equals("alice"))
        {
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            String eavesdrop = (String)session.getAttribute("eavesdrop");
            byte[] converted_SharedKey = d_alice.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            byte[] encryptedMessage = aesEncryption.encrypt(message.getBytes(StandardCharsets.UTF_8));
            d_bob.messages.add("Alice:" + encryptedMessage);
            eavesdrop+=(encryptedMessage+"\n");
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            String eavesdrop = (String)session.getAttribute("eavesdrop");
            byte[] converted_SharedKey = d_bob.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            byte[] encryptedMessage = aesEncryption.encrypt(message.getBytes(StandardCharsets.UTF_8));
            d_alice.messages.add("Bob:" + encryptedMessage);
            eavesdrop+=(encryptedMessage+"\n");
        }
        return "index.jsp";
    }

    @GetMapping("/deCryptMsg")
    public String sendMsg(@RequestParam("user") String user, HttpSession session)
    {
        AESEncryption aesEncryption = new AESEncryption();
        if(user.equals("alice"))
        {
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            byte[] converted_SharedKey = d_alice.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            for (Object message : d_alice.messages)
            {
                if(message.getClass().isArray())
                {
                    byte[] encryptedMessage = (byte[]) message;
                    String decodeMsg = new String(aesEncryption.decrypt(encryptedMessage), StandardCharsets.UTF_8);
                    d_alice.messages.remove(message);
                    d_alice.messages.add(decodeMsg);
                }
            }
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            byte[] converted_SharedKey = d_bob.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            //byte[] encryptedMessage = aesEncryption.encrypt(message.getBytes(StandardCharsets.UTF_8));
            //d_alice.messages.add("Bob:" + encryptedMessage);
        }
        return "index.jsp";
    }
}
