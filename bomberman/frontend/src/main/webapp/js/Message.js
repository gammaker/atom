Messages = Class.extend({
    handler: {},

    init: function () {
        this.handler['C'] = this.handlePawn;
        this.handler['B'] = this.handleBomb;
        this.handler['b'] = this.handleBonus;
        this.handler['X'] = this.handleWood;
        this.handler['W'] = this.handleWall;
        this.handler['F'] = this.handleFire;
        this.handler['M'] = this.handleMove;
        this.handler['D'] = this.handleDestroy;
    },

    handleReplica: function (msg) {
        var events = msg.split('\n');

        for (var i = 0; i < events.length; i++) {
            var event = events[i];

            if (!event.trim()) continue;

            //split event in format type(params)
            var bracketPos = event.data.indexOf('(');
            var type = event.data.substr(0, bracketPos);

            if (gMessages.handler[type] === undefined) {
                console.log('Unknown event type ' + type + '. Event: ' + event);
                continue;
            }

            var params = event.data.substr(bracketPos + 1, event.data.length - bracketPos - 2);
            gMessages.handler[type](params.split(','));
        }
    },

    handlePossess: function (msg) {
        gInputEngine.possessed = parseInt(msg);
    },

    findAnyObject: function (id) {
        var obj = gGameEngine.players.find(function (el) {
            return el.id === id;
        });
        if (obj) return obj;
        obj = gGameEngine.bombs.find(function (el) {
            return el.id === id;
        });
        if (obj) return obj;
        obj = gGameEngine.bonuses.find(function (el) {
            return el.id === id;
        });
        if (obj) return obj;
        obj = gGameEngine.fires.find(function (el) {
            return el.id === id;
        });
        if (obj) return obj;
        obj = gGameEngine.tiles.find(function (el) {
            return el.id === id;
        });
        if (obj) return obj;
        return null;
    },

    handleMove: function (params) {
        var id = parseInt(params[0]);
        var obj = this.findAnyObject(id);
        if (!obj) return;
        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});
        obj.bmp.x = position.x;
        obj.bmp.y = position.y;
    },

    handleDestroy: function (params) {
        var id = parseInt(params[0]);
        var obj = this.findAnyObject(id);
        if (!obj) return;
        if (obj.die !== undefined) obj.die();
        obj.remove();
    },

    handlePawn: function(params) {
        var id = parseInt(params[0]);
        var player = gGameEngine.players.find(function (el) {
            return el.id === id;
        });
        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});

        if (player) {
            player.bmp.x = position.x;
            player.bmp.y = position.y;
        } else {
            console.log(new Date().getTime() + " handel new player " + obj.id);
            player = new Player(id, position);
            gGameEngine.players.push(player);
        }
    },

    handleBomb: function(params) {
        var id = parseInt(params[0]);
        var bomb = gGameEngine.bombs.find(function (el) {
            return el.id === id;
        });
        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});

        if (bomb) {
            bomb.bmp.x = position.x;
            bomb.bmp.y = position.y;
        } else {
            bomb = new Bomb(id, position);
            gGameEngine.bombs.push(bomb);
        }
    },

    handleWood: function (params) {
        var id = parseInt(params[0]);
        var tile = gGameEngine.tiles.find(function (el) {
            return el.id === id;
        });

        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});
        if (tile) {
            tile.material = 'Wood';
        } else {
            tile = new Tile(id, 'Wood', position);
            gGameEngine.tiles.push(tile);
        }
    },

    handleWall: function (params) {
        var id = parseInt(params[0]);
        var tile = gGameEngine.tiles.find(function (el) {
            return el.id === id;
        });

        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});
        if (tile) {
            tile.material = 'Wall';
        } else {
            tile = new Tile(id, 'Wall', position);
            gGameEngine.tiles.push(tile);
        }
    },

    handleFire: function (params) {
        var id = parseInt(params[0]);
        var fire = gGameEngine.fires.find(function (el) {
            return el.id === id;
        });

        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});
        if (!fire) {
            fire = new Fire(id, position);
            gGameEngine.fires.push(fire);
        }
    },

    handleBonus: function (params) {
        var id = parseInt(params[0]);
        var bonus = gGameEngine.bonuses.find(function (el) {
            return el.id === id;
        });
        var position = Utils.getEntityPosition({x: parseInt(params[1]), y: parseInt(params[2])});

        if (bonus) {
            bonus.bmp.x = position.x;
            bonus.bmp.y = position.y;
        } else {
            bonus = new Bonus(id, position, parseInt(params[3]));
            gGameEngine.bonuses.push(bonus);
        }
    }

});

gMessages = new Messages();
