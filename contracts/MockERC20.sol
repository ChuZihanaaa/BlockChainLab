// SPDX-License-Identifier: Apache 2.0
pragma solidity ^0.8.20;contract ERC20Test {
    // 代币元数据
    string private _name = "MJYToken";
    string private _symbol = "MJY2";
    uint8 private _decimals = 18;

    // 代币总量和余额
    uint256 private _totalSupply; // 总量
    mapping(address => uint256) private _balances; // 地址余额
    mapping(address => mapping(address => uint256)) private _allowances; // 授权额度

    // 事件
    event Transfer(address indexed from, address indexed to, uint256 value); // 转账
    event Approval(address indexed owner, address indexed spender, uint256 value); // 授权

    // 构造函数，初始化代币并分配初始供应量
    constructor(uint256 initialSupply) {
        mint(msg.sender, initialSupply);
    }

    // 返回代币名称
    function name() public view returns (string memory) {
        return _name;
    }

    // 返回代币符号
    function symbol() public view returns (string memory) {
        return _symbol;
    }

    // 返回代币精度
    function decimals() public view returns (uint8) {
        return _decimals;
    }

    // 返回代币总供应量
    function totalSupply() public view returns (uint256) {
        return _totalSupply;
    }

    // 查询指定地址的代币余额
    function balanceOf(address account) public view returns (uint256) {
        return _balances[account];
    }

    // 向指定地址转账
    function transfer(address to, uint256 value) public returns (bool) {
        require(to != address(0), unicode"ERC20: 转账目标地址不能为零地址");
        require(_balances[msg.sender] >= value, unicode"ERC20: 转账金额超过余额");

        _balances[msg.sender] -= value;
        _balances[to] += value;
        emit Transfer(msg.sender, to, value);
        return true;
    }

    // 授权指定地址使用一定数量的代币
    function approve(address spender, uint256 value) public returns (bool) {
        require(spender != address(0), unicode"ERC20: 授权目标地址不能为零地址");

        _allowances[msg.sender][spender] = value;
        emit Approval(msg.sender, spender, value);
        return true;
    }

    // 从一个地址向另一个地址转账（需授权）
    function transferFrom(address from, address to, uint256 value) public returns (bool) {
        require(from != address(0), unicode"ERC20: 转账来源地址不能为零地址");
        require(to != address(0), unicode"ERC20: 转账目标地址不能为零地址");
        require(_balances[from] >= value, unicode"ERC20: 转账金额超过余额");
        require(_allowances[from][msg.sender] >= value, unicode"ERC20: 转账金额超过授权额度");

        _balances[from] -= value;
        _balances[to] += value;
        _allowances[from][msg.sender] -= value;
        emit Transfer(from, to, value);
        return true;
    }

    // 查询某地址可使用的授权额度
    function allowance(address owner, address spender) public view returns (uint256) {
        return _allowances[owner][spender];
    }

    // 铸造代币到指定地址
    function mint(address account, uint256 value) public returns (bool) {
        require(account != address(0), unicode"ERC20: 铸造目标地址不能为零地址");

        _totalSupply += value;
        _balances[account] += value;
        emit Transfer(address(0), account, value);
        return true;
    }

    // 销毁调用者的代币
    function burn(uint256 value) public returns (bool) {
        require(_balances[msg.sender] >= value, unicode"ERC20: 销毁金额超过余额");

        _balances[msg.sender] -= value;
        _totalSupply -= value;
        emit Transfer(msg.sender, address(0), value);
        return true;
    }
}