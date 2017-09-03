package com.cometproject.server.game.navigator.types.search;

import com.cometproject.api.game.rooms.settings.RoomAccessType;
import com.cometproject.server.game.groups.GroupManager;
import com.cometproject.server.game.groups.types.Group;
import com.cometproject.server.game.groups.types.GroupData;
import com.cometproject.server.game.navigator.NavigatorManager;
import com.cometproject.server.game.navigator.types.Category;
import com.cometproject.server.game.navigator.types.publics.PublicRoom;
import com.cometproject.server.game.players.components.types.messenger.MessengerFriend;
import com.cometproject.server.game.players.types.Player;
import com.cometproject.server.game.rooms.RoomManager;
import com.cometproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.cometproject.server.game.rooms.types.RoomData;
import com.cometproject.server.game.rooms.types.RoomPromotion;
import com.cometproject.server.network.messages.outgoing.navigator.updated.NavigatorSearchResultSetMessageComposer;
import com.cometproject.server.tasks.CometTask;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NavigatorSearchService implements CometTask {
    private static NavigatorSearchService searchServiceInstance;

    private Executor searchExecutor = Executors.newFixedThreadPool(8);

    public NavigatorSearchService() {
//        CometThreadManager.getInstance().executePeriodic(this, 0, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        // TODO: Cache navigator search results.
    }

    public void submitRequest(Player player, String category, String data) {
        this.searchExecutor.execute(() -> {
            if(data.isEmpty()) {
                // send categories.
                List<Category> categoryList = Lists.newArrayList();

                for(Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                    if (navigatorCategory.getCategory().equals(category)) {
                        if (navigatorCategory.isVisible() && !navigatorCategory.getCategoryType().toString().toLowerCase().equals("my_favorites") && !navigatorCategory.getCategoryType().toString().toLowerCase().equals("with_rights") && !navigatorCategory.getCategoryType().toString().toLowerCase().equals("with_friends") && !navigatorCategory.getCategoryType().toString().toLowerCase().equals("my_groups") && !navigatorCategory.getCategoryType().toString().toLowerCase().equals("my_friends_rooms"))
                            categoryList.add(navigatorCategory);
                    }

                    if (category.equals("myworld_view")) {
                        if (navigatorCategory.getCategoryType().toString().toLowerCase().equals("my_friends_rooms")) {
                            boolean friendsRoomsNotEmpty = false;

                            for (MessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                                if (friendsRoomsNotEmpty) {
                                    continue;
                                }

                                if (messengerFriend.isInRoom()) {
                                    PlayerEntity playerEntity = messengerFriend.getSession().getPlayer().getEntity();

                                    if (playerEntity != null) {
                                        if (playerEntity.getRoom().getData().getOwnerId() == playerEntity.getPlayerId()) {
                                            if (playerEntity.getRoom().getData().getAccess() == RoomAccessType.INVISIBLE && player.getData().getRank() < 3) {
                                                if (playerEntity.getRoom().getGroup() != null) {
                                                    continue;
                                                } else {
                                                    if (!playerEntity.getRoom().getRights().hasRights(player.getId())) {
                                                        continue;
                                                    }
                                                }
                                            }

                                            friendsRoomsNotEmpty = true;
                                        }
                                    }
                                }
                            }

                            if (friendsRoomsNotEmpty) {
                                categoryList.add(navigatorCategory);
                            }
                        }

                        if (navigatorCategory.getCategoryType().toString().toLowerCase().equals("with_friends")) {
                            boolean withFriendsRoomsNotEmpty = false;

                            for (MessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                                if (withFriendsRoomsNotEmpty) {
                                    continue;
                                }

                                if (messengerFriend.isInRoom()) {
                                    PlayerEntity playerEntity = messengerFriend.getSession().getPlayer().getEntity();

                                    if (playerEntity != null && !playerEntity.getPlayer().getSettings().getHideOnline()) {
                                        if (playerEntity.getRoom().getData().getAccess() == RoomAccessType.INVISIBLE && player.getData().getRank() < 3) {
                                            final Group group = GroupManager.getInstance().getGroupByRoomId(playerEntity.getRoom().getId());

                                            if (playerEntity.getRoom().getGroup() != null) {
                                                if (!player.getGroups().contains(group.getId())) {
                                                    continue;
                                                }
                                            } else {
                                                if (!playerEntity.getRoom().getRights().hasRights(player.getId())) {
                                                    continue;
                                                }
                                            }
                                        }

                                        withFriendsRoomsNotEmpty = true;
                                    }
                                }
                            }

                            if (withFriendsRoomsNotEmpty) {
                                categoryList.add(navigatorCategory);
                            }
                        }

                        if (navigatorCategory.getCategoryType().toString().toLowerCase().equals("my_groups")) {
                            boolean groupHomeRoomsNotEmpty = false;

                            for (int groupId : player.getGroups()) {
                                if (groupHomeRoomsNotEmpty) {
                                    continue;
                                }

                                GroupData groupData = GroupManager.getInstance().getData(groupId);

                                if (groupData != null) {
                                    RoomData roomData = RoomManager.getInstance().getRoomData(groupData.getRoomId());

                                    if (roomData != null) {
                                        groupHomeRoomsNotEmpty = true;
                                    }
                                }
                            }

                            if (groupHomeRoomsNotEmpty) {
                                categoryList.add(navigatorCategory);
                            }
                        }

                        if (navigatorCategory.getCategoryType().toString().toLowerCase().equals("with_rights") && player.getRoomsWithRights().size() > 0) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                if(categoryList.size() == 0) {
                    for(Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                        if(navigatorCategory.getCategoryType().toString().toLowerCase().equals(category) && navigatorCategory.isVisible()) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                if(categoryList.size() == 0) {
                    for(Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                        if(navigatorCategory.getCategoryId().equals(category) && navigatorCategory.isVisible()) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                player.getSession().send(new NavigatorSearchResultSetMessageComposer(category, data, categoryList, player));
            } else {
                player.getSession().send(new NavigatorSearchResultSetMessageComposer("hotel_view", data, null, player));
            }
        });
    }

    public List<RoomData> search(Category category, Player player, boolean expanded) {
        List<RoomData> rooms = Lists.newCopyOnWriteArrayList();

        switch (category.getCategoryType()) {
            case MY_ROOMS:
                if (player.getRooms() == null) {
                    break;
                }

                for (Integer roomId : new LinkedList<>(player.getRooms())) {
                    if (RoomManager.getInstance().getRoomData(roomId) == null) continue;

                    rooms.add(RoomManager.getInstance().getRoomData(roomId));
                }
                break;

            case MY_FAVORITES:
                List<RoomData> favouriteRooms = Lists.newArrayList();

                if(player.getNavigator() == null) {
                    return rooms;
                }

                for(Integer roomId : player.getNavigator().getFavouriteRooms()) {
                    if(favouriteRooms.size() == 50) break;

                    final RoomData roomData = RoomManager.getInstance().getRoomData(roomId);

                    if(roomData != null) {
                        favouriteRooms.add(roomData);
                    }
                }

                rooms.addAll(order(favouriteRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                favouriteRooms.clear();
                break;

            case POPULAR:
                rooms.addAll(order(RoomManager.getInstance().getRoomsByCategory(-1, 1, player), expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                break;

            case CATEGORY:
                rooms.addAll(order(RoomManager.getInstance().getRoomsByCategory(category.getId(), player), expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                break;

            case TOP_PROMOTIONS:
                List<RoomData> promotedRooms = Lists.newArrayList();

                for (RoomPromotion roomPromotion : RoomManager.getInstance().getRoomPromotions().values()) {
                    if (roomPromotion != null) {
                        RoomData roomData = RoomManager.getInstance().getRoomData(roomPromotion.getRoomId());

                        if (roomData != null) {
                            promotedRooms.add(roomData);
                        }
                    }
                }

                rooms.addAll(order(promotedRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                promotedRooms.clear();
                break;

            case PUBLIC:
                for (PublicRoom publicRoom : NavigatorManager.getInstance().getPublicRooms().values()) {
                    RoomData roomData = RoomManager.getInstance().getRoomData(publicRoom.getRoomId());

                    if (roomData != null) {
                        rooms.add(roomData);
                    }
                }
                break;

            case STAFF_PICKS:
                List<RoomData> staffPicks = Lists.newArrayList();

                for (int roomId : NavigatorManager.getInstance().getStaffPicks()) {
                    RoomData roomData = RoomManager.getInstance().getRoomData(roomId);

                    if (roomData != null) {
                        staffPicks.add(roomData);
                    }
                }

                rooms.addAll(order(staffPicks, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                staffPicks.clear();
                break;

            case MY_GROUPS:
                List<RoomData> groupHomeRooms = Lists.newArrayList();

                for (int groupId : player.getGroups()) {
                    GroupData groupData = GroupManager.getInstance().getData(groupId);

                    if (groupData != null) {
                        RoomData roomData = RoomManager.getInstance().getRoomData(groupData.getRoomId());

                        if (roomData != null) {
                            groupHomeRooms.add(roomData);
                        }
                    }
                }

                rooms.addAll(order(groupHomeRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                groupHomeRooms.clear();
                break;

            case MY_FRIENDS_ROOMS:
                List<RoomData> friendsRooms = Lists.newArrayList();

                if (player.getMessenger() == null) {
                    return rooms;
                }

                for (MessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                    if (messengerFriend.isInRoom()) {
                        PlayerEntity playerEntity = messengerFriend.getSession().getPlayer().getEntity();

                        if (playerEntity != null) {
                            if (!friendsRooms.contains(playerEntity.getRoom().getData())) {
                                if (playerEntity.getRoom().getData().getOwnerId() == playerEntity.getPlayerId()) {
                                    if (playerEntity.getRoom().getData().getAccess() == RoomAccessType.INVISIBLE && player.getData().getRank() < 3) {
                                        if (playerEntity.getRoom().getGroup() != null) {
                                            continue;
                                        } else {
                                            if (!playerEntity.getRoom().getRights().hasRights(player.getId())) {
                                                continue;
                                            }
                                        }
                                    }

                                    friendsRooms.add(playerEntity.getRoom().getData());
                                }
                            }
                        }
                    }
                }

                rooms.addAll(order(friendsRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                friendsRooms.clear();
                break;

            case WITH_FRIENDS:
                List<RoomData> withFriendsRooms = Lists.newArrayList();

                if(player.getMessenger() == null) {
                    return rooms;
                }

                for(MessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                    if (messengerFriend.isInRoom()) {
                        PlayerEntity playerEntity = messengerFriend.getSession().getPlayer().getEntity();

                        if (playerEntity != null && !playerEntity.getPlayer().getSettings().getHideOnline()) {
                            if (!withFriendsRooms.contains(playerEntity.getRoom().getData())) {
                                if (playerEntity.getRoom().getData().getAccess() == RoomAccessType.INVISIBLE && player.getData().getRank() < 3) {
                                    final Group group = GroupManager.getInstance().getGroupByRoomId(playerEntity.getRoom().getId());

                                    if (playerEntity.getRoom().getGroup() != null) {
                                        if (!player.getGroups().contains(group.getId())) {
                                            continue;
                                        }
                                    } else {
                                        if (!playerEntity.getRoom().getRights().hasRights(player.getId())) {
                                            continue;
                                        }
                                    }
                                }

                                withFriendsRooms.add(playerEntity.getRoom().getData());
                            }
                        }
                    }
                }

                rooms.addAll(order(withFriendsRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                withFriendsRooms.clear();
                break;

            case WITH_RIGHTS:
                if(player.getRoomsWithRights() == null) {
                    break;
                }

                for (Integer roomId : new LinkedList<>(player.getRoomsWithRights())) {
                    if (RoomManager.getInstance().getRoomData(roomId) == null) continue;

                    rooms.add(RoomManager.getInstance().getRoomData(roomId));
                }
                break;
        }

        return rooms;
    }

    public static List<RoomData> order(List<RoomData> rooms, int limit) {
        try {
            Collections.sort(rooms, (room1, room2) -> {
                boolean is1Active = RoomManager.getInstance().isActive(room1.getId());
                boolean is2Active = RoomManager.getInstance().isActive(room2.getId());

                return ((!is2Active ? 0 : RoomManager.getInstance().get(room2.getId()).getEntities().playerCount()) -
                        (!is1Active ? 0 : RoomManager.getInstance().get(room1.getId()).getEntities().playerCount()));
            });
        } catch (Exception ignored) {

        }

        List<RoomData> returnRooms = new LinkedList<>();

        for (RoomData roomData : rooms) {
            if (returnRooms.size() >= limit) {
                break;
            }

            returnRooms.add(roomData);
        }

        return returnRooms;
    }

    public static NavigatorSearchService getInstance() {
        if (searchServiceInstance == null) {
            searchServiceInstance = new NavigatorSearchService();
        }

        return searchServiceInstance;
    }
}