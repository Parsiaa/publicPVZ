import java.util.EnumMap;
import java.util.Map;
import controllers.CollectionMenuController;
import controllers.GameEngine;
import controllers.GameMenuController;
import controllers.GreenhouseMenuController;
import controllers.LeaderboardController;
import controllers.LoginMenuController;
import controllers.MainMenuController;
import controllers.MatchCheatController;
import controllers.MenuRouter;
import controllers.NewsMenuController;
import controllers.PlantSelectionMenuController;
import controllers.ProfileMenuController;
import controllers.SettingsMenuController;
import controllers.ShopMenuController;
import controllers.SignUpMenuController;
import controllers.TravelLogController;
import models.Enums.Menu;
import models.User;
import utils.UserApp;
import views.AppView;
import views.CollectionMenuView;
import views.GameMenuView;
import views.GreenhouseMenuView;
import views.LeaderboardMenuView;
import views.LoginMenuView;
import views.MainMenuView;
import views.MenuView;
import views.NewsMenuView;
import views.PlantSelectionMenuView;
import views.ProfileMenuView;
import views.SettingsMenuView;
import views.ShopMenuView;
import views.SignUpMenuView;
import views.TravelLogMenuView;

public class Main {

    public static void main(String[] args) {
        UserApp userApp = new UserApp();
        MenuRouter router = new MenuRouter();

        SignUpMenuController signUpController = new SignUpMenuController(userApp);
        LoginMenuController loginController = new LoginMenuController(userApp);
        MainMenuController mainMenuController = new MainMenuController(userApp, router);
        SettingsMenuController settingsController = new SettingsMenuController(userApp);
        ProfileMenuController profileController = new ProfileMenuController(userApp);
        NewsMenuController newsController = new NewsMenuController(userApp);
        CollectionMenuController collectionController = new CollectionMenuController(userApp);
        GameMenuController gameMenuController = new GameMenuController(userApp, router);
        PlantSelectionMenuController plantSelectionController =
                new PlantSelectionMenuController(userApp, router, collectionController);
        GreenhouseMenuController greenhouseController =
                new GreenhouseMenuController(userApp, router, collectionController);
        ShopMenuController shopController =
                new ShopMenuController(userApp, greenhouseController, collectionController);
        TravelLogController travelLogController = new TravelLogController(userApp);
        LeaderboardController leaderboardController = new LeaderboardController(userApp);
        GameEngine gameEngine = new GameEngine(userApp, router);
        MatchCheatController cheatController = new MatchCheatController(gameEngine);
        plantSelectionController.setGameEngine(gameEngine);
        gameMenuController.setGameEngine(gameEngine);
        gameMenuController.setSettingsController(settingsController);
        travelLogController.setGameEngine(gameEngine);
        travelLogController.setRouter(router);

        Map<Menu, MenuView> views = new EnumMap<>(Menu.class);
        views.put(Menu.SignUpMenu, new SignUpMenuView(router, signUpController));
        views.put(Menu.LoginMenu, new LoginMenuView(router, loginController));
        views.put(Menu.MainMenu, new MainMenuView(router, mainMenuController, gameMenuController));
        views.put(Menu.GameMenu, new GameMenuView(router, gameMenuController, gameEngine, cheatController));
        views.put(Menu.SettingsMenu, new SettingsMenuView(router, settingsController));
        views.put(Menu.ProfileMenu, new ProfileMenuView(router, profileController));
        views.put(Menu.NewsMenu, new NewsMenuView(router, newsController));
        views.put(Menu.CollectionMenu, new CollectionMenuView(router, collectionController));
        views.put(Menu.PlantSelectionMenu,
                new PlantSelectionMenuView(router, plantSelectionController, settingsController));
        views.put(Menu.GreenhouseMenu, new GreenhouseMenuView(router, greenhouseController));
        views.put(Menu.ShopMenu, new ShopMenuView(router, shopController));
        views.put(Menu.TravelLogMenu, new TravelLogMenuView(router, travelLogController));
        views.put(Menu.LeaderboardMenu, new LeaderboardMenuView(router, leaderboardController));
        views.put(Menu.NetworkMenu, new MenuView(router) {
            @Override
            protected boolean handleCommand(String input) {
                return false;
            }
        });

        User rememberedUser = userApp.findStayLoggedInUser();
        if (rememberedUser != null) {
            userApp.setLoggedInUser(rememberedUser);
            router.navigateTo(Menu.MainMenu);
            System.out.println("Welcome back, " + rememberedUser.getUsername() + "! (still logged in)");
        }

        new AppView(router, views).run();
        userApp.saveUsers();
    }
}
