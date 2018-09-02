const http = require('http');
var inquirer = require('inquirer');
var readline = require('readline');
var request = require('request');

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
                name: 'conhost',
                message: 'Choose host to connect to'
            },
            {
                type: 'text',
                name: 'conport',
                message: 'Choose port to connect to',
                default: '9461'
            },
            {
                type: 'text',
                name: 'host',
                message: 'Choose host to listen on',
                default: 'localhost'
            },
            {
                type: 'text',
                name: 'port',
                message: 'Choose port to listen on',
                default: '9461'
            }
        ]).then(answers => {
            do_connect(parseInt(answers.conport, 10), answers.conhost, parseInt(answers.port, 10), answers.host)
        })
    }
})

function do_connect(port, host, cport, chost) {

    request.post(
        `http://${host}:${port}/auth`,
        { json: { port: cport, host: chost } },
        ( err, response, body ) => {
            if ( !err && response.statusCode == 200) {
                console.log(body)
            } else {
                console.log(err)
            }
        }
    )
    console.log("posted?")
    inquire()
}

function do_serve(PORT, HOST) {
    function request_handler(req, res) {
        let body = ""
        req.on('readable', () => {
            body += req.read()
        })
        console.log(req)
        req.on('end', () => {
            if ( req.url  == "/auth" ) {
                authData = JSON.parse(body)
                var conIp = authData.ip
                var conPort = authData.port
                res.end("yeetus")
                do_connect(PORT, HOST, conPort, conIp)
            } else {
                message = JSON.parse(body).msg
                console.log(message)
                res.end("yeetus")
            }
        })
    }
    const server = http.createServer(request_handler)

    server.listen(PORT, (err) => {
        if ( err ) { console.log(err) }
    })
}

function send_message(host, port, message) {
    request.post(
        `http::/${host}:${port}`,
        { json: { msg: message } },
        ( err, response, body ) => {
            if ( !err && response.statusCode == 200) {
                console.log(body)
            }
        }
    )
}

function inquire (host, port) {
    inquirer.prompt([
        {
            type: 'text',
            name: 'message',
            message: '>'
        }
    ]).then(answers => {
        send_message(host, port, answers.message)
        inquire()
    })
}