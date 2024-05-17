package com.skyhorsemanpower.auction.repository.cqrs.command;

import com.skyhorsemanpower.auction.domain.cqrs.command.CommandOnlyAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;

@EnableReactiveMongoRepositories
public interface CommandOnlyAuctionRepository extends JpaRepository<CommandOnlyAuction, Long> {
}
