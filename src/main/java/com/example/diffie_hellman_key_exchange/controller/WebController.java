package com.example.diffie_hellman_key_exchange.controller;

import com.example.diffie_hellman_key_exchange.logic.AESEncryption;
import com.example.diffie_hellman_key_exchange.model.DiffieHellman;
import com.example.diffie_hellman_key_exchange.model.DiffieHellmanRepo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class WebController {
    @GetMapping("/")
    public String loginPage(HttpSession session)
    {
        DiffieHellman d_alice = new DiffieHellman(5);
        session.setAttribute("d_alice",d_alice);
        DiffieHellman d_bob = /*DiffieHellmanRepo.getInstance(5).getDiffieHellman();*/new DiffieHellman(5);
        session.setAttribute("d_bob",d_bob);
        List<Object> eavesdrops = new ArrayList<>();
        session.setAttribute("eavesdrops", eavesdrops);
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
            List<Object> eavesdrops = (List<Object>)session.getAttribute("eavesdrops");
            d_alice.genSharedKey(d_bob.getPublic_key());
            eavesdrops.add(d_bob.getPublic_key());
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            List<Object> eavesdrops = (List<Object>)session.getAttribute("eavesdrops");
            d_bob.genSharedKey(d_alice.getPublic_key());
            eavesdrops.add(d_alice.getPublic_key());
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
            List<Object> eavesdrops = (List<Object>)session.getAttribute("eavesdrops");
            byte[] converted_SharedKey = d_alice.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            byte[] encryptedMessage = aesEncryption.encrypt(message.getBytes(StandardCharsets.UTF_8));
            d_bob.messages.add(/*"Alice:" + */encryptedMessage);
            eavesdrops.add(encryptedMessage);
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            DiffieHellman d_alice = (DiffieHellman)session.getAttribute("d_alice");
            List<Object> eavesdrops = (List<Object>)session.getAttribute("eavesdrops");
            byte[] converted_SharedKey = d_bob.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            byte[] encryptedMessage = aesEncryption.encrypt(message.getBytes(StandardCharsets.UTF_8));
            d_alice.messages.add(/*"Bob:" + */encryptedMessage);
            eavesdrops.add(encryptedMessage);
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
            List<Object> temporaryDecodedMessages = new ArrayList<>(); //We need this because we need to replace the bytearrays with strings and we cannot do that in the same list while iterating through it
            for (Object message : d_alice.messages)
            {
                if(message.getClass().isArray())
                {
                    byte[] encryptedMessage = (byte[]) message;
                    String decodeMsg = new String(aesEncryption.decrypt(encryptedMessage), StandardCharsets.UTF_8);
                    temporaryDecodedMessages.add(decodeMsg);
                }
                else
                {
                    temporaryDecodedMessages.add(message);
                }
            }
            d_alice.messages=temporaryDecodedMessages;
        }
        else if(user.equals("bob"))
        {
            DiffieHellman d_bob = (DiffieHellman)session.getAttribute("d_bob");
            byte[] converted_SharedKey = d_bob.getShared_key().toByteArray(); // We need this to create a Bouncy Castle cipher key with the proper length (128/192/256 bits) for the crypting
            byte[] key = new byte[32];
            for (int i = 0; i < 32; i++) {
                key[i]=converted_SharedKey[i];
            } // Chopping it to the proper size as I mentioned above
            aesEncryption.createKey(key); //Creating a key based on the shared key
            List<Object> temporaryDecodedMessages = new ArrayList<>(); //We need this because we need to replace the bytearrays with strings
            for (Object message : d_bob.messages)
            {
                if(message.getClass().isArray())
                {
                    byte[] encryptedMessage = (byte[]) message;
                    String decodeMsg = new String(aesEncryption.decrypt(encryptedMessage), StandardCharsets.UTF_8);
                    temporaryDecodedMessages.add(decodeMsg);
                }
                else
                {
                    temporaryDecodedMessages.add(message);
                }
            }
            d_bob.messages=temporaryDecodedMessages;
        }
        return "index.jsp";
    }
}
