var Utils = {};

/**
 * Returns true if positions are equal.
 */
Utils.comparePositions = function(pos1, pos2) {
    return pos1.x == pos2.x && pos1.y == pos2.y;
};


/**
 * Convert bitmap pixels position to entity on grid position.
 */
Utils.convertToEntityPosition = function(pixels) {
    var position = {};
    position.x = Math.round(pixels.x / gGameEngine.tileSize);
    position.y = Math.round(pixels.y /gGameEngine.tileSize);
    return position;
};

Utils.getEntityPosition = function (pixels) {
    var position = {};
    position.x = pixels.x;
    position.y = -pixels.y + 12 * 32;
    return position;
}

/**
 * Convert entity on grid position to bitmap pixels position.
 */
Utils.convertToBitmapPosition = function(entity) {
    var position = {};
    position.x = entity.x * gGameEngine.tileSize;
    position.y = entity.y * gGameEngine.tileSize;
    return position;
};

/**
 * Removes an item from array.
 */
Utils.removeFromArray = function(array, item) {
    for (var i = 0; i < array.length; i++) {
        if (item == array[i]) {
            array.splice(i, 1);
        }
    }
    return array;
};

Utils.getParameterByName = function(name, url)
{
    if(!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)");
    var results = regex.exec(url);
    if(!results) return null;
    if(!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
};
