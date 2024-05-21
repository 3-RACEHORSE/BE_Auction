package com.skyhorsemanpower.auction.domain.cqrs.command;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommandOnlyAuction is a Querydsl query type for CommandOnlyAuction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommandOnlyAuction extends EntityPathBase<CommandOnlyAuction> {

    private static final long serialVersionUID = -1538545559L;

    public static final QCommandOnlyAuction commandOnlyAuction = new QCommandOnlyAuction("commandOnlyAuction");

    public final com.skyhorsemanpower.auction.common.QBaseCreateAndEndTimeEntity _super = new com.skyhorsemanpower.auction.common.QBaseCreateAndEndTimeEntity(this);

    public final NumberPath<Long> auctionPostId = createNumber("auctionPostId", Long.class);

    public final StringPath auctionUuid = createString("auctionUuid");

    public final StringPath bidderUuid = createString("bidderUuid");

    public final NumberPath<Integer> bidPrice = createNumber("bidPrice", Integer.class);

    public final StringPath category = createString("category");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> endedAt = _super.endedAt;

    public final NumberPath<Integer> minimumBiddingPrice = createNumber("minimumBiddingPrice", Integer.class);

    public final StringPath sellerUuid = createString("sellerUuid");

    public final EnumPath<com.skyhorsemanpower.auction.status.AuctionStateEnum> state = createEnum("state", com.skyhorsemanpower.auction.status.AuctionStateEnum.class);

    public final StringPath title = createString("title");

    public QCommandOnlyAuction(String variable) {
        super(CommandOnlyAuction.class, forVariable(variable));
    }

    public QCommandOnlyAuction(Path<? extends CommandOnlyAuction> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommandOnlyAuction(PathMetadata metadata) {
        super(CommandOnlyAuction.class, metadata);
    }

}

