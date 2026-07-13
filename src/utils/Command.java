package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum Command {

    MENU_ENTER("^menu enter (?<name>\\S+)$"),
    MENU_EXIT("^menu exit$"),
    MENU_SHOW_CURRENT("^menu show current$"),
    MENU_LOGOUT("^menu logout$"),

    REGISTER("^register -u (?<username>\\S+) -p (?<password>\\S+) (?<confirm>\\S+)"
            + " -n (?<nickname>.+?) -e (?<email>\\S+) -g (?<gender>\\S+)$"),
    PICK_QUESTION("^pick question -q (?<number>\\d+) -a (?<answer>.+?) -c (?<confirm>.+)$"),
    LOGIN("^login -u (?<username>\\S+) -p (?<password>\\S+)(?<stay> -stay-logged-in)?$"),
    FORGET_PASSWORD("^forget password -u (?<username>\\S+) -e (?<email>\\S+)$"),
    ANSWER("^answer -a (?<answer>.+)$"),
    SET_NEW_PASSWORD("^set password -p (?<password>\\S+)$"),

    MENU_ENTER_CHAPTER("^menu enter chapter -c (?<chapter>\\S+)$"),
    MENU_GREENHOUSE("^menu greenhouse$"),
    MENU_TRAVEL_LOG("^menu travel-log$"),
    MENU_LEADERBOARD("^menu leaderboard$"),
    MENU_COIN_WALLET("^menu coin-wallet$"),
    MENU_GEM_WALLET("^menu gem-wallet$"),
    MENU_CHEAT_ADD("^menu cheat add (?<amount>\\d+) (?<currency>coin|diamond)$"),

    CHANGE_DIFFICULTY("^menu settings change-difficulty -l (?<level>-?\\d+)$"),

    NEWS_SHOW_UNREAD("^menu news show-unread$"),
    NEWS_SHOW_ALL("^menu news show-all$"),

    PROFILE_CHANGE_USERNAME("^menu profile change-username -u (?<username>\\S+)$"),
    PROFILE_CHANGE_NICKNAME("^menu profile change-nickname -u (?<nickname>.+)$"),
    PROFILE_CHANGE_EMAIL("^menu profile change-email -e (?<email>\\S+)$"),
    PROFILE_CHANGE_PASSWORD("^menu profile change-password -p (?<newpass>\\S+) -o (?<oldpass>\\S+)$"),
    PROFILE_SHOW_INFO("^menu profile show-info$"),

    COLLECTION_SHOW_PLANTS("^menu collection show-plants$"),
    COLLECTION_SHOW_ALL_PLANTS("^menu collection show-all-plants$"),
    COLLECTION_SHOW_ZOMBIES("^menu collection show-zombies$"),
    COLLECTION_SHOW_ALL_ZOMBIES("^menu collection show-all-zombies$"),
    COLLECTION_SHOW_PLANT("^menu collection show-plant -p (?<plant>.+)$"),
    COLLECTION_SHOW_ZOMBIE("^menu collection show-zombie -z (?<zombie>.+)$"),
    COLLECTION_UPGRADE_PLANT("^menu collection upgrade-plant -p (?<plant>.+)$"),
    COLLECTION_PURCHASE_PLANT("^menu collection purchase-plant -p (?<plant>.+)$"),

    SHOW_ALL_PLANTS("^show all plants$"),
    SHOW_AVAILABLE_PLANTS("^show available plants$"),
    ADD_PLANT("^add plant -t (?<type>.+)$"),
    REMOVE_PLANT("^remove plant -t (?<type>.+)$"),
    BOOST_PLANT("^boost plant -t (?<type>.+)$"),
    START_GAME("^start game$"),

    ADVANCE_TIME("^advance time -t (?<count>-?\\d+) ticks?$"),
    PLANT_PLANT("^plant plant -t (?<type>.+?) -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    PLUCK_PLANT("^pluck plant -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    FEED_PLANT("^feed plant -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    COLLECT_SUN("^collect sun -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    SHOW_SUN_AMOUNT("^show sun amount$"),
    SHOW_MAP("^show map$"),
    SHOW_PLANTS_STATUS("^show plants status$"),
    SHOW_TILE_STATUS("^show tile status -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    ZOMBIES_INFO("^zombies info$"),
    START_ZOMBIE_WAVES("^start zombie waves$"),
    CHEAT_ADD_SUNS("^cheat add -n (?<count>-?\\d+) suns$"),
    CHEAT_SPAWN_ZOMBIE("^cheat spawn-zombie -t (?<type>.+?) -l \\(?(?<x>\\d+), ?(?<y>\\d+)\\)?$"),
    CHEAT_NUKE("^release the nuke$"),
    CHEAT_REMOVE_COOLDOWN("^cheat remove-cooldown$"),
    CHEAT_ADD_PLANT_FOOD("^cheat add-plant-food$"),

    SHOW_GREENHOUSE("^show greenhouse$"),
    PLANT_POT("^plant pot at \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    COLLECT_POT("^collect \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    GROW_POT("^grow \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    ENTER_SHOP("^enter shop$"),

    SHOP_LIST("^shop list$"),
    SHOP_DAILY("^shop daily$"),
    SHOP_BUY("^shop buy -i (?<item>\\d+) -n (?<count>-?\\d+)( -t (?<type>.+))?$"),

    TRAVEL_LOG_PAGE("^travel log page (?<page>\\S+)$"),
    TRAVEL_LOG_CLAIM("^travel log claim$"),
    PLAY_MINIGAME("^play minigame -m (?<name>\\S+) -s (?<stage>\\d+)$"),

    BREAK_VASE("^break vase -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    PLACE_ZOMBIE("^place zombie -t (?<type>.+?) -l \\((?<x>\\d+), ?(?<y>\\d+)\\)$"),
    SCORE_GAME("^start score game$"),

    SHOW_LEADERBOARD("^show leaderboard$"),
    LEADERBOARD_SORT("^sort by (?<column>level|minigames|quests|meowpoints) (?<order>asc|desc)$");

    private final Pattern pattern;

    Command(String regex) {
        this.pattern = Pattern.compile(regex);
    }

    public Matcher getMatcher(String input) {
        return pattern.matcher(input);
    }
    public Matcher match(String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches() ? matcher : null;
    }
}
