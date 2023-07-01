package org.dokat.systemclans.management;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.dokat.systemclans.ConfigManager;
import org.dokat.systemclans.SystemClans;
import org.dokat.systemclans.dbmanagement.controllers.ClanController;
import org.dokat.systemclans.dbmanagement.controllers.DataController;
import org.dokat.systemclans.dbmanagement.controllers.PlayerController;
import org.dokat.systemclans.utils.Utility;

import java.util.HashMap;
import java.util.UUID;

/**
 * Класс ClanInviteManager управляет отправкой и принятием приглашений в клан.
 */
public class ClanInviteManager implements Utility {
    private HashMap<UUID, BukkitRunnable> inviteTasks;
    private HashMap<UUID, UUID> pendingInvites;

    private final ConfigManager config = new ConfigManager();
    private final String playerAlreadyHasInvite = config.getMessages("player_already_has_invite");
    private final String invitationSentToPlayer = config.getMessages("invitation_sent_to_player");
    private final String playerNotRespondInvitation = config.getMessages("player_not_respond_invitation");
    private final String playerInvitesToClan = config.getMessages("player_invites_to_clan");
    private final String playerAcceptedInvitation = config.getMessages("player_accepted_invitation");
    private final String joinedClan = config.getMessages("joined_clan");
    private final String youDontHaveInvitations = config.getMessages("you_dont_have_invitations");
    private final String playerAdded = config.getMessages("player_added");

    /**
     * Конструктор класса ClanInviteManager.
     * Инициализирует коллекции для хранения задач ожидания и ожидающих приглашений.
     */
    public ClanInviteManager() {
        this.inviteTasks = new HashMap<>();
        this.pendingInvites = new HashMap<>();
    }

    /**
     * Отправляет приглашение в клан от отправителя к целевому игроку.
     *
     * @param sender       отправитель приглашения
     * @param targetPlayer целевой игрок, которому отправляется приглашение
     */
    public void sendInvite(Player sender, Player targetPlayer) {
        UUID senderId = sender.getUniqueId();
        UUID targetId = targetPlayer.getUniqueId();

        String senderName = sender.getName();
        String targetName = targetPlayer.getName();

        // Проверка, что игрок уже не имеет активного приглашения
        if (pendingInvites.containsKey(targetId)) {
            sender.sendMessage(color(playerAlreadyHasInvite));
            return;
        }

        // Отправка приглашения
        sender.sendMessage(color(invitationSentToPlayer).replace("{targetUserName}", targetName));
        targetPlayer.sendMessage(color(playerInvitesToClan)
                .replace("{userName}", senderName)
                .replace("{clanName}", PlayerController.getPlayer(senderName).getClanName()));

        // Создание и запуск задачи ожидания
        BukkitRunnable inviteTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (pendingInvites.containsKey(targetId)) {
                    // Удаление приглашения и отправка сообщения об отмене
                    pendingInvites.remove(targetId);
                    sender.sendMessage(color(playerNotRespondInvitation).replace("{targetUserName}", targetName));
                }
            }
        };
        inviteTasks.put(targetId, inviteTask);
        pendingInvites.put(targetId, senderId);
        inviteTask.runTaskLater(SystemClans.getInstance(), 20 * 40);
    }

    public void acceptInvite(Player player) {
        UUID playerId = player.getUniqueId();

        if (pendingInvites.containsKey(playerId)) {
            // Остановка задачи ожидания
            BukkitRunnable inviteTask = inviteTasks.get(playerId);
            if (inviteTask != null) {
                inviteTask.cancel();
                inviteTasks.remove(playerId);
            }

            // Получение и удаление данных о приглашении
            UUID senderId = pendingInvites.remove(playerId);
            Player sender = Bukkit.getPlayer(senderId);
            String targetName = player.getName();

            // Сохранение данных в базе данных


            String clanName = PlayerController.getPlayer(sender.getName()).getClanName();
            PlayerController.save(new org.dokat.systemclans.dbmanagement.data_models.Player(targetName, clanName));
//            DataController.addPlayer(new org.dokat.systemclans.dbmanagement.data_models.Player(targetName, clanName));

            sendMessageEveryone(clanName, playerAdded.replace("{targetUserName}", targetName), targetName);

            // Отправка сообщения об успешном принятии приглашения
            if (sender != null) {
                sender.sendMessage(color(playerAcceptedInvitation).replace("{targetUserName}", targetName));
            }

            player.sendMessage(color(joinedClan).replace("{clanName}", clanName));
            if (ClanController.getClan(clanName).getWelcomeMessage() != null){
                player.sendMessage(color(ClanController.getClan(clanName).getWelcomeMessage()
                        .replace("[player]", targetName)));
            }

        } else {
            player.sendMessage(color(youDontHaveInvitations));
        }
    }
}