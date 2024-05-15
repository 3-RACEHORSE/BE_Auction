package com.skyhorsemanpower.auction.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuctionCategory is a Querydsl query type for AuctionCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuctionCategory extends EntityPathBase<AuctionCategory> {

    private static final long serialVersionUID = -2072434628L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuctionCategory auctionCategory = new QAuctionCategory("auctionCategory");

    public final NumberPath<Long> auctionCategoryid = createNumber("auctionCategoryid", Long.class);

    public final StringPath auctionUuid = createString("auctionUuid");

    public final QCategory category;

    public QAuctionCategory(String variable) {
        this(AuctionCategory.class, forVariable(variable), INITS);
    }

    public QAuctionCategory(Path<? extends AuctionCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuctionCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuctionCategory(PathMetadata metadata, PathInits inits) {
        this(AuctionCategory.class, metadata, inits);
    }

    public QAuctionCategory(Class<? extends AuctionCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
    }

}

