package com.skyhorsemanpower.auction.repository.command;

import com.skyhorsemanpower.auction.domain.command.CommandOnlyAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandOnlyAuctionRepository extends JpaRepository<CommandOnlyAuction, Long> {
}
