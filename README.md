# pear

wip p2p chat

## Installation

Download `jar` from releases page. 

## Usage

Start application 
```sh
$ java -jar pear-0.1.0-standalone.jar [args]
```

## Options

Pear does not accept any arguments


### Bugs

This project is still too early in development to have a coherent bug list.

### Progress

Pear is a peer to peer chat program that uses a private blockchain to send and receive messages between peers. Currently, pear's blockchain creation, and validation is complete, however the sharing is still in progress. The user interface will use Seesaw to display all messages in the blockchain, a text box to send new messages, and an interface to join an existing chain by specifying a user's IP in the chain.

This is my first serious Clojure program, so please excuse messy code. I chose clojure because it runs on JVM so programs written with it can run on most modern operating systems, and because of the large amount of support and the friendly and helpful community. 

**So what does it do so far?**

So far, pear has the following features, see [roadmap](./#roadmap) for more info.
- Creates a blockchain, and validates it. There is no proof of work algorithm implemented as of yet.
- Creates an extremely basic UI, and renders the list to it. The list does not yet update with the chain
- Uses Ring to listen for HTTP requests, and responds to *some* accordingly.
- Allows peers to join the chain, but does not yet notify peers of new users joining. 
- Allows requests of current chain, and shares chain.

### Roadmap

- Finish UI
- Finish HTTP server
- Allow custom ports
- Test it to make sure this actually works
- Add POW algorithm using `:nonce` field in blocks
- Fix recursion in peer sharing

## License

Copyright © 2018 swissChili

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
