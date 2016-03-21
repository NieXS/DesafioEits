Stockontrol.controller('MainController',function($scope, $mdSidenav, $http, $state, $window)
{
	console.log($state);
	/*
	 * Modelo
	 */
	$scope.model = {};
	$scope.currentUser;

	/** Mantém a URL da parcial do cabeçalho **/
	$scope.header = {};

	/** Usuário atual **/

	/*
	 * Métodos
	 */

	$scope.openLogoutMenu = function($mdOpenMenu, $event)
	{
		$mdOpenMenu($event);
	};

	$scope.logout = function()
	{
		$window.location.href = "/logout";
	};

	$scope.openMenu = function()
	{
		$mdSidenav('leftMenu').open();
	};

	$scope.goTo = function(where)
	{
		$mdSidenav('leftMenu').close().then(function()
		{
			$state.go(where);
		});
	};

	/*
	 * Inicialização
	 */

	userService.getCurrent(function(data)
	{
		$scope.currentUser = data;
		$scope.currentUser.admin = function()
		{
			return this.profile == 'Administrator';
		}
	});
});
