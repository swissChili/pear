var net = require('net');
var inquirer = require('inquirer');
var readline = require('readline');

inquirer.prompt([
    {
        type: 'list',
        name: 'method',
        message: 'Create channel, or connect?',
        choices: [
            'Create',
            'Connect'
        ]
    }
]).then(answers => {
    if (answers.method == 'Create') {
        inquirer.prompt([
            {
                type: 'text',
                name: 'host',
                message: 'Choose the host',
                default: 'localhost'
            },
            {
                type: 'text',
                message: 'Choose the port',
                name: 'port',
                default: '9461'
            }
        ]).then(answers => {
            console.log("starting...")
            do_serve(parseInt(answers.port, 10), answers.host)
        })
    } else {
        inquirer.prompt([
            {
                type: 'text',
                name: 'host',
                message: 'Choose host to connect to'
            },
            {
                type: 'text',
                name: 'port',
                message: 'Choose port to connect to',
                default: '9461'
            }
        ]).then(answers => {
            do_connect(parseInt(answers.port, 10), answers.host)
        })
    }
})

function do_connect(port, host) {
    let socket = net.createConnection(port, host);
    socket.on("data", data => {
        console.log(data)
    })
    function inquire () {
        inquirer.prompt([
            {
                type: 'text',
                name: 'message',
                message: '>'
            }
        ]).then(answers => {
            socket.write(answers.message);
            inquire()
        })
    }
    inquire()
}

function do_serve(PORT, HOST) {
    net.createServer(function(sock) {
        // Add a 'data' event handler to this instance of socket
        sock.on('data', function(data) {
            
            console.log('DATA ' + sock.remoteAddress + ': ' + data);
            // Write the data back to the socket, the client will receive it as data from the server
            sock.write('You said "' + data + '"');
            
        });

        function inquire () {
            inquirer.prompt([
                {
                    type: 'text',
                    name: 'message',
                    message: ''
                }
            ]).then(answers => {
                sock.write(answers.message);
                inquire()
            })
        }
        inquire()
        
        // Add a 'close' event handler to this instance of socket
        sock.on('close', function(data) {
            console.log('CLOSED: ' + sock.remoteAddress +' '+ sock.remotePort);
        });
        
    }).listen(PORT, HOST);

    console.log('Server listening on ' + HOST +':'+ PORT);
}