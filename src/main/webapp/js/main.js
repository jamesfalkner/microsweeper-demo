/*----- constants -----*/
var bombImage = '<img src="images/bomb.png">';
var flagImage = '<img src="images/flag.png">';
var wrongBombImage = '<img src="images/wrong-bomb.png">'
var sizeLookup = {
  '9': {totalBombs: 10, tableWidth: '245px'},
  '16': {totalBombs: 40, tableWidth: '420px'},
  '30': {totalBombs: 160, tableWidth: '794px'}
};
var colors = [
  '',
  '#0000FA',
  '#4B802D',
  '#DB1300',
  '#202081',
  '#690400',
  '#457A7A',
  '#1B1B1B',
  '#7A7A7A',
];

/*----- app's state (variables) -----*/
var size = 16;
var level = 'medium';
var board;
var bombCount;
var timeElapsed;
var adjBombs;
var hitBomb;
var elapsedTime;
var timerId;
var winner;

/*----- cached element references -----*/
var boardEl = document.getElementById('board');

/*----- event listeners -----*/
document.getElementById('size-btns').addEventListener('click', function (e) {
  size = parseInt(e.target.id.replace('size-', ''));
  level = (size < 15) ? 'easy' : ((size < 20) ? 'medium' : 'hard');
  init();
  render();
});

boardEl.addEventListener('click', function (e) {
  if (winner || hitBomb) return;
  var clickedEl;
  clickedEl = e.target.tagName.toLowerCase() === 'img' ? e.target.parentElement : e.target;
  if (clickedEl.classList.contains('game-cell')) {
    if (!timerId) setTimer();
    var row = parseInt(clickedEl.dataset.row);
    var col = parseInt(clickedEl.dataset.col);
    var cell = board[row][col];
    if (e.shiftKey && !cell.revealed && bombCount > 0) {
      bombCount += cell.flag() ? -1 : 1;
    } else {
      hitBomb = cell.reveal();
      if (hitBomb) {
        revealAll();
        clearInterval(timerId);
        e.target.style.backgroundColor = 'red';
      }
    }
    winner = getWinner();
    render();
  }
});

function createResetListener() {
  document.getElementById('reset').addEventListener('click', function () {
    init();
    render();
  });
}

/*----- functions -----*/
function setTimer() {
  timerId = setInterval(function () {
    elapsedTime += 1;
    document.getElementById('timer').innerText = elapsedTime.toString().padStart(3, '0');
  }, 1000);
};

function revealAll() {
  board.forEach(function (rowArr) {
    rowArr.forEach(function (cell) {
      cell.reveal();
    });
  });
};

var currentName = "";

function setCurrentName(name) {
  currentName = name;
  console.log("current name now: " + name);
}

function buildTable() {

  var topRow = `
  <tr>
    <td class="menu" id="window-title-bar" colspan="${size}">
      <div id="window-title"><img src="images/mine-menu-icon.png"> Minesweeper</div>
      <div id="window-controls"><img onclick="clearScores()" src="images/window-controls.png"></div>
    </td>
  <tr>
    <td class="menu" id="folder-bar" colspan="${size}">
      <div id="folder1"><a href="https://github.com/nickarocho/minesweeper/blob/master/readme.md" target="blank">Read Me </a></div>
      <div id="folder2"><a href="https://github.com/nickarocho/minesweeper" target="blank">Source Code</a></div>
      <div id="folder3"><input onchange="setCurrentName(this.value)" type="text" id="playerName" placeholder="Player Name..." width="50px"/></div>
    </td>
  </tr>
  </tr>
    <tr>
      <td class="menu" colspan="${size}">
          <section id="status-bar">
            <div id="bomb-counter">000</div>
            <div id="reset"><img src="images/smiley-face.png"></div>
            <div id="timer">000</div>
          </section>
      </td>
    </tr>
    `;
  boardEl.innerHTML = topRow + `<tr>${'<td class="game-cell"></td>'.repeat(size)}</tr>`.repeat(size);
  document.getElementById('playerName').value = currentName;
  boardEl.style.width = sizeLookup[size].tableWidth;
  createResetListener();
  var cells = Array.from(document.querySelectorAll('td:not(.menu)'));
  cells.forEach(function (cell, idx) {
    cell.setAttribute('data-row', Math.floor(idx / size));
    cell.setAttribute('data-col', idx % size);
  });
}

function buildArrays() {
  var arr = Array(size).fill(null);
  arr = arr.map(function () {
    return new Array(size).fill(null);
  });
  return arr;
};

function addScore(score) {

  let tbody = document.getElementById('scoretablebody');
  var row = document.createElement("tr");
  ['name', 'time', 'level', 'success'].forEach(function(col) {
    var cell = document.createElement("td");
    cell.className = 'menu';
    let cellText = document.createTextNode(score[col]);
    cell.appendChild(cellText);
    row.appendChild(cell);
  });
  tbody.appendChild(row);

}

function clearScores() {
  try {

    let xhr = new XMLHttpRequest();
    xhr.open('DELETE', '/api/scoreboard');
    xhr.onload = function () {
      if (xhr.status === 204) {
        location.reload();
      }
      else {
        alert('Clear failed.  Returned status of ' + xhr.status);
      }
    };
    xhr.send();
  } catch (ex) {
    console.log(ex);
  }

}
function buildScores() {

  try {

    let xhr = new XMLHttpRequest();
    xhr.open('GET', '/api/scoreboard');
    xhr.onload = function () {
      if (xhr.status === 200) {

        let scores = JSON.parse(xhr.responseText);
        document.getElementById('scoretablebody').innerHTML = '';

        scores.forEach(function (score) {
          addScore(score);
        });
      }
      else {
        alert('Request failed.  Returned status of ' + xhr.status);
      }
    };
    xhr.send();
  } catch (ex) {
    console.log(ex);
  }

}

function buildCells() {
  board.forEach(function (rowArr, rowIdx) {
    rowArr.forEach(function (slot, colIdx) {
      board[rowIdx][colIdx] = new Cell(rowIdx, colIdx, board);
    });
  });
  addBombs();
  runCodeForAllCells(function (cell) {
    cell.calcAdjBombs();
  });
};

function init() {
  buildTable();
  board = buildArrays();
  buildCells();
  bombCount = getBombCount();
  elapsedTime = 0;
  clearInterval(timerId);
  timerId = null;
  hitBomb = false;
  winner = false;
  buildScores();
};

function getBombCount() {
  var count = 0;
  board.forEach(function (row) {
    count += row.filter(function (cell) {
      return cell.bomb;
    }).length
  });
  return count;
};

function addBombs() {
  var currentTotalBombs = sizeLookup[`${size}`].totalBombs;
  while (currentTotalBombs !== 0) {
    var row = Math.floor(Math.random() * size);
    var col = Math.floor(Math.random() * size);
    var currentCell = board[row][col]
    if (!currentCell.bomb) {
      currentCell.bomb = true
      currentTotalBombs -= 1
    }
  }
};

function getWinner() {
  for (var row = 0; row < board.length; row++) {
    for (var col = 0; col < board[0].length; col++) {
      var cell = board[row][col];
      if (!cell.revealed && !cell.bomb) return false;
    }
  }
  return true;
};

function render() {
  document.getElementById('bomb-counter').innerText = bombCount.toString().padStart(3, '0');
  var seconds = timeElapsed % 60;
  var tdList = Array.from(document.querySelectorAll('[data-row]'));
  tdList.forEach(function (td) {
    var rowIdx = parseInt(td.getAttribute('data-row'));
    var colIdx = parseInt(td.getAttribute('data-col'));
    var cell = board[rowIdx][colIdx];
    if (cell.flagged) {
      td.innerHTML = flagImage;
    } else if (cell.revealed) {
      if (cell.bomb) {
        td.innerHTML = bombImage;
      } else if (cell.adjBombs) {
        td.className = 'revealed';
        td.style.color = colors[cell.adjBombs];
        td.textContent = cell.adjBombs;
      } else {
        td.className = 'revealed'
      }
    } else {
      td.innerHTML = '';
    }
  });
  if (hitBomb) {
    document.getElementById('reset').innerHTML = '<img src=images/dead-face.png>';
    runCodeForAllCells(function (cell) {
      if (!cell.bomb && cell.flagged) {
        var td = document.querySelector(`[data-row="${cell.row}"][data-col="${cell.col}"]`);
        td.innerHTML = wrongBombImage;
      }
    });

    recordScore(currentName, elapsedTime, level, false);
  } else if (winner) {
    recordScore(currentName, elapsedTime, level, true);
    document.getElementById('reset').innerHTML = '<img src=images/cool-face.png>';
    clearInterval(timerId);
  }
};

function recordScore(name, time, level, won) {

  let xhr = new XMLHttpRequest();

  xhr.open('POST', '/api/scoreboard');
  xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  xhr.onload = function () {
    if (xhr.status === 200 || xhr.status === 204) {
      console.log("request success: " + xhr.responseText);
    }
    else {
      console.log("request fail: " + xhr.status);
    }
  };

  let score = {
    name: name,
    time: time,
    level: level,
    success: won
  };

  addScore(score);
  console.log("recording score: " + JSON.stringify(score));
  xhr.send(JSON.stringify(score));

}

function runCodeForAllCells(cb) {
  board.forEach(function (rowArr) {
    rowArr.forEach(function (cell) {
      cb(cell);
    });
  });
}

init();
render();