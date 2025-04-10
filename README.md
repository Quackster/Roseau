**Roseau**

*Habbo Hotel v1 server, 2001 client revision*

Roseau has been a small project I worked on for a few months now and I've finally come to the point where this is pretty damn complete, far surpassing the original v1 servers that have already been released. 

If you're wondering where the development thread of this went, I got sick and tired of constantly creating development threads and never finishing the project so I decided to delete it, and complete it and then release it as a surprise to the community.

This server is written in Java, and uses libraries such as Netty (old version, like 3.x.x) and other libraries that allow MySQL connection pooling, which is BoneCP.

**Key features**

- Diving works with ticket purchase and voting
- Teleporters work (in same room and different rooms)
- Ranked display works (rank 1 to 5)
- More public rooms working than any other v1 server released (which either had none or one working).
- Wall items working (no other v1 server had this!)
- Instant console messaging (no other v1 server had this either).

**Features**

###Register
- Check for existing names
- Check for bad names
- Create new user

###User
- Login
- Edit user details (figure, email, etc)

###Navigator

- Lists all public rooms
- Clicking on a public room shows the users in each room
- Shows all recently created private rooms with users in a room at the top, the list is scrollable too
- Search rooms
- List own rooms
- Hides room owner names if the option had been ticked

###Messenger
- Search users on console
- Send user a friend request
- Accept friend request
- Reject friend request
- Send friend message (and can offline message)

###Private room

- Create private room through public room room-o-matics
- Edit room details
- Lock user room
- Ring doorbell of locked room
- Password protect room
- Delete room

###Public Room

- 12 public rooms added
- Main Lobby
- Median Lobby
- Skylight Lobby
- Basement Lobby
- Club Slinky Helsinki (with walkway to second club room)
- Habbo Lido
- Habbo Lido II
- Club Massiva (with walkway to downstairs disco floor)
- Theatredome
- Habburger's
- The Dirty Duck Pub
- Cunning Fox Gamehall (with walkways to all game rooms)
- Cafe Ole
- Hotel Kitchen

All public rooms are fully furnished to what official Habbo had

Walkways between rooms work (Habbo Lido to the diving deck, Club Massiva downstairs disco floor, etc)

Room-o-Matic works

Sitting on furniture in public rooms

Bots in public rooms (Habburger's, Cafe Ole, The Dirty Duck Pub)

Disco lights in Club Massiva working]

###Lido and Diving Deck
- Change clothes working (with curtain closing)
- Pool lift door closes and opens depending if a user is inside or not.
- Buying tickets work for self and other players.
- Diving.
- Swimming.
- Queue works (line up on first tile and the user automatically walks when there is a free spot).

###Item

- Place room items
- Move and rotate room items
- Pickup room item
- Place wall items
- Pickup wall items
- Place stickies
- Update stickies
- Stack items
- Teleporters work
- Fridges work (grabbing a drink from a fridge)
- Turning items on/off (with rights)
- Randomisation of the rotation of the bottle when it's spun

###Catalogue

- All items are purchasable
- Purchase posters
- Place floor and wall items to decorate wall and floors of private rooms


###Ranked features

- Call for help
- Alert call for help to Hobba staff (picked up call for help not coded).


###Commands

- :about
- :sit


[/LIST]

**Ranks**

(These badges will appear on your user inside rooms).


- Rank 1: Normal rank
- Rank 2: Bronze Hobba
- Rank 3: Silver Hobba
- Rank 4: Gold Hobba
- Rank 5: Staff administrator (Habbo staff badge)

**Permissions**

- Minimum rank 5:

- room_all_rights


- Minimum rank 2: 

- room_kick_any_user
- answer_call_for_help

**Screenshots**

![image](https://github.com/user-attachments/assets/105442d0-5f61-4a91-b126-e343bc31e433)

![image](https://github.com/user-attachments/assets/70c7154b-64ba-4e43-af04-66ea88adaae1)

![image](https://github.com/user-attachments/assets/3887c8ce-5078-4004-acf9-00d2d51ae435)

![image](https://github.com/user-attachments/assets/5a7a1cae-e330-4ee7-ad0e-fb095b41cd27)

![image](https://github.com/user-attachments/assets/68ec2961-5d54-458c-a553-fa603fad558f)

![image](https://github.com/user-attachments/assets/c70059a7-f96a-4f1f-ae4a-5c8cc5c59102)

**Source repository**

Download: [url]https://github.com/Quackster/Roseau/archive/master.zip[/url]

The repository includes the client files, the loader and the MySQL database for the server.

Compiled version can be found in /Roseau-bin/ and the client can be found in /client/ folder with a loader (should be called index.html).

**Shockwave Tips**

I personally use Pale Moon portable 32 bit version (it has to be 32 bit otherwise it won't work). Shockwave works flawless in this browser. The download is only 31 MB.

Pale Moon 32bit: https://www.palemoon.org/palemoon-portable.shtml

Alternatively you can use an older version of Mozilla Firefox as 52 has most NAPI functions disabled but 36-38 will work, or Internet Explorer (not recommended to use IE due to the fact it freezes all the time).

**Code Snippets**

*PoolLiftInteractor*

```
package org.alexdev.roseau.game.item.interactors.pool;

import org.alexdev.roseau.game.item.Item;
import org.alexdev.roseau.game.item.interactors.Interaction;
import org.alexdev.roseau.game.player.Player;
import org.alexdev.roseau.messages.outgoing.JUMPINGPLACE_OK;

public class PoolLiftInteractor extends Interaction {

	public PoolLiftInteractor(Item item) {
		super(item);
	}

	@Override
	public void onTrigger(Player player) {	}

	@Override
	public void onStoppedWalking(Player player) {
		
		this.close();

		player.send(new JUMPINGPLACE_OK());
		player.getRoomUser().setCanWalk(false);

		player.getDetails().setTickets(player.getDetails().getTickets() - 1);
		player.getDetails().sendTickets();
		player.getDetails().save();
	}
	
	public void open() {
		this.item.showProgram("open");
		this.item.unlockTiles();
	}
	
	public void close() {
		this.item.showProgram("close");
		this.item.lockTiles();	
	}

}
```

**Credits**

With Ascii from Puomi Hotel, these things were possible:

- wall items loading
- the correct ITEMS structure
- figuring out the correct structure for ACTIVE OBJECTS
- SHOWPROGRAM for the Club Massiva disco lights
- correct structure for teleporters flashing
- teaching me how to edit the DCR to enable Club Slinky Helsinki to work
- 
And lab-hotel from RaGEZONE helped me out with:

- Instant console messaging.
- Enabling the debug window for the version 1 client.

And lastly, office.boy and Nillus who made my life easier with their Blunk v5 server, it helped me
with some protocol that was simillar to v1, and their item definition database which was very helpful for
the v1 catalogue.

Thanks guys, this is the most completed v1 server to date (if you ignore gamehall rooms)!

- Alex
